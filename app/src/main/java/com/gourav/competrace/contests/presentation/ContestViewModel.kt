package com.gourav.competrace.contests.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.R
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.remote.CodeforcesRepository
import com.gourav.competrace.app_core.util.*
import com.gourav.competrace.contests.data.ContestAlarmScheduler
import com.gourav.competrace.contests.data.repository.ContestAlarmRepository
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.contests.model.ContestAlarmItem
import com.gourav.competrace.contests.model.ContestScreenState
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.settings.util.ScheduleNotifBeforeOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences,
    private val contestAlarmRepository: ContestAlarmRepository,
    private val contestAlarmScheduler: ContestAlarmScheduler,
) : ViewModel() {

    val contestSites = Sites.values().filter { it.isContestSite }

    private val _selectedIndex = userPreferences.selectedContestSiteIndexFlow

    private val codeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    fun addContestToContestListById(codeforcesContest: CompetraceContest) {
        codeforcesDatabase.addContestToContestListById(contest = codeforcesContest)
    }

    fun setSelectedIndexTo(value: Int) {
        viewModelScope.launch {
            userPreferences.setSelectedContestSiteIndex(value)
        }
    }

    private val _notificationContestIds = MutableStateFlow(setOf<String>())

    private val scheduleNotifBefore = userPreferences.scheduleNotifBeforeFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ScheduleNotifBeforeOptions.OneHour.value
    )

    private val _allContests = MutableStateFlow<List<CompetraceContest>>(emptyList())

    private fun clearAllContests() {
        _allContests.update { emptyList() }
    }

    private fun addContestToAllContests(contest: CompetraceContest) {
        _allContests.update { it + contest }
    }

    private val _screenState = MutableStateFlow(ContestScreenState())
    val screenState = _screenState
        .combine(_selectedIndex) { state, index ->
            if(index >= contestSites.size) state.copy(selectedIndex = 0)
            else state.copy(selectedIndex = index)
        }
        .combine(_allContests) { state, allContests ->
            val currentSite = contestSites[state.selectedIndex].title
            val currentContests = allContests.filter { it.site == currentSite }
            state.copy(
                ongoingContests = currentContests.filter { it.isOngoing() },
                next7DaysContests = currentContests.filter { it.isWithin7Days() },
                after7DaysContests = currentContests.filter { it.isAfter7Days() }
            )
        }
        .combine(_notificationContestIds) { state, notificationContestIds ->
            state.copy(notificationContestIds = notificationContestIds)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ContestScreenState()
        )

    fun getContestListFromCodeforces() {
        viewModelScope.launch {
            codeforcesRepository.getContestList(gym = false)
                .onStart {
                    _screenState.update { it.copy(apiState = ApiState.Loading) }
                }
                .catch { e ->
                    val errorMessage = ErrorEntity.getError(e).messageId
                    _screenState.update {
                        it.copy(apiState = ApiState.Failure(UiText.StringResource(errorMessage)))
                    }
                    Log.e(TAG, "getContestListFromKontests: ${e.cause}")
                }
                .collect { apiResult ->
                    if (apiResult.status == "OK") {

                        // To add contest to database
                        apiResult.result?.map {
                            it.mapToCompetraceContest()
                        }?.forEach { contest ->
                            contest.ratedCategories.clear()
                            ContestRatedCategories.values().forEach {
                                if (contest.name.contains(it.value)) contest.ratedCategories.add(it)
                            }
                            addContestToContestListById(codeforcesContest = contest)
                        }

                        // To display Upcoming Contests
                        clearAllContests()
                        val contests = apiResult.result
                            ?.map {
                                it.mapToCompetraceContest()
                            }
                            ?.filter {
                                if (it.phase == Phase.BEFORE)
                                    it.startTimeInMillis >= TimeUtils.currentTimeInMillis()
                                else
                                    it.endTimeInMillis >= TimeUtils.currentTimeInMillis()
                            }
                            ?.sortedBy {
                                it.startTimeInMillis
                            }

                        contests?.forEach { contest ->
                            contest.ratedCategories.clear()
                            ContestRatedCategories.values().forEach {
                                if (contest.name.contains(it.value)) contest.ratedCategories.add(it)
                            }
                            addContestToAllContests(contest = contest)

                            if (contest.startTimeInMillis - TimeUtils.currentTimeInMillis() >
                                TimeUtils.minutesToMillis(scheduleNotifBefore.value)
                            )
                                contestAlarmRepository.updateAlarm(
                                    contest.getAlarmItem(scheduleNotifBefore.value)
                                )
                        }

                        _screenState.update { it.copy(apiState = ApiState.Success) }
                        Log.d(TAG, "Codeforces - Got - Contest List")
                    } else {
                        Log.e(TAG, "getContestListFromCodeforces: " + apiResult.comment.toString())
                        _screenState.update {
                            it.copy(apiState = ApiState.Failure(UiText.DynamicString("Something Went Wrong!")))
                        }
                    }
                }

            delay(1000)
            // Load Gym Contests.
            codeforcesRepository.getContestList(gym = true)
                .catch {
                    Log.e(TAG, "getContestListFromCodeforcesGym: ${it.cause}")
                }
                .collect { apiResult ->
                    if (apiResult.status == "OK") {
                        apiResult.result?.map {
                            it.mapToCompetraceContest()
                        }?.forEach { contest ->
                            codeforcesDatabase.addContestToContestListById(contest = contest)
                        }

                        Log.d(TAG, "Codeforces - Got - Gym Contest List")
                    } else {
                        Log.e(TAG, "getContestListFromCodeforcesGym: " + apiResult.comment.toString())
                    }
                }
        }
    }

    fun toggleContestNotification(contest: CompetraceContest) {
        val contestTimeRemInMillis = contest.startTimeInMillis - TimeUtils.currentTimeInMillis()

        if (contest.id.toString() in _notificationContestIds.value) {
            val item = contest.getAlarmItem(scheduleNotifBefore.value)

            viewModelScope.launch {
                contestAlarmRepository.deleteAlarm(item)
                item.let(contestAlarmScheduler::cancel)
            }
            _notificationContestIds.update { it - contest.id.toString() }
        } else {

            if (contestTimeRemInMillis > TimeUtils.minutesToMillis(scheduleNotifBefore.value)) {
                val item = contest.getAlarmItem(scheduleNotifBefore.value)

                viewModelScope.launch {
                    contestAlarmRepository.addAlarm(item)
                    item.let(contestAlarmScheduler::schedule)
                }
                _notificationContestIds.update { it + contest.id.toString() }

                SnackbarManager.showMessage(
                    message = UiText.StringResource(
                        R.string.conf_notif_set,
                        ScheduleNotifBeforeOptions.getOption(scheduleNotifBefore.value)
                    )
                )
            } else if (contestTimeRemInMillis > TimeUtils.minutesToMillis(10)) {
                val item = contest.getAlarmItem(10)

                SnackbarManager.showMessageWithAction(
                    message = UiText.StringResource(R.string.ask_to_set_notif),
                    actionLabel = UiText.StringResource(R.string.yes),
                    action = {
                        viewModelScope.launch {
                            contestAlarmRepository.addAlarm(item)
                            item.let(contestAlarmScheduler::schedule)
                        }
                        _notificationContestIds.update { it + contest.id.toString() }
                    }
                )
            } else {
                SnackbarManager.showMessage(UiText.StringResource(R.string.contest_starting_in_ten))
            }
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            contestAlarmRepository.getAllAlarms().collect { list ->
                list.forEach { alarm ->
                    contestAlarmScheduler.cancel(alarm)
                    _notificationContestIds.update { it - alarm.contestId }
                }
            }
            contestAlarmRepository.deleteALlAlarms()
        }

        SnackbarManager.showMessage(UiText.StringResource(R.string.all_notif_cleared))
    }

    init {
        getContestListFromCodeforces()

        viewModelScope.launch {
            val alarmsToDelete = mutableListOf<ContestAlarmItem>()

            contestAlarmRepository.getAllAlarms().collect { list ->
                list.forEach { alarm ->
                    if (alarm.timeInMillis > TimeUtils.currentTimeInMillis()) {
                        contestAlarmScheduler.schedule(alarm)
                        _notificationContestIds.update { it + alarm.contestId }
                    } else alarmsToDelete.add(alarm)
                }
            }

            alarmsToDelete.forEach {
                contestAlarmRepository.deleteAlarm(it)
            }
        }
    }

    companion object {
        private const val TAG = "Contest ViewModel"
    }
}