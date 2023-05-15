package com.gourav.competrace.contests.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.R
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.remote.KontestsRepository
import com.gourav.competrace.app_core.util.*
import com.gourav.competrace.contests.data.ContestAlarmScheduler
import com.gourav.competrace.contests.data.repository.ContestAlarmRepository
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.contests.model.ContestAlarmItem
import com.gourav.competrace.contests.model.ContestScreenState
import com.gourav.competrace.settings.util.ScheduleNotifBeforeOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestViewModel @Inject constructor(
    private val kontestsRepository: KontestsRepository,
    private val userPreferences: UserPreferences,
    private val contestAlarmRepository: ContestAlarmRepository,
    private val contestAlarmScheduler: ContestAlarmScheduler,
) : ViewModel() {

    val contestSites = Sites.values().filter { it.isContestSite }

    private val _selectedIndex = userPreferences.selectedContestSiteIndexFlow

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
            state.copy(selectedIndex = index)
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

    fun getContestListFromKontests() {
        viewModelScope.launch {
            kontestsRepository.getAllContests()
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
                    clearAllContests()

                    val contests = apiResult
                        .filter { contest ->
                            contest.site?.let { site ->
                                contestSites.any { it.title == site }
                            } ?: false
                        }.map {
                            it.mapToCompetraceContest()
                        }.sortedBy {
                            it.startTimeInMillis
                        }

                    contests.forEach { contest ->
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
                    Log.d(TAG, "Kontests - Got - Contest List")
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
        getContestListFromKontests()

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