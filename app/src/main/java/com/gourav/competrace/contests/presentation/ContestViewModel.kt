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
import com.gourav.competrace.settings.util.ScheduleNotifBeforeOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    val selectedIndex = userPreferences.selectedContestSiteIndexFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0
    )

    fun setSelectedIndexTo(value: Int) {
        viewModelScope.launch {
            userPreferences.setSelectedContestSiteIndex(value)
        }
    }

    private val selectedSite = selectedIndex.map {
        contestSites[it]
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Sites.Codeforces
    )

    private val _notificationContestIdList = MutableStateFlow(setOf<String>())
    val notificationContestIdList = _notificationContestIdList.asStateFlow()

    private val scheduleNotifBefore = userPreferences.scheduleNotifBeforeFlow.map {
        it
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        60
    )

    private val _responseForKontestsContestList = MutableStateFlow<ApiState>(ApiState.Loading)
    val responseForKontestsContestList = _responseForKontestsContestList.asStateFlow()

    private val _isKontestsContestListRefreshing = MutableStateFlow(false)
    val isKontestsContestListRefreshing = _isKontestsContestListRefreshing.asStateFlow()

    private val _allContests = MutableStateFlow<List<CompetraceContest>>(emptyList())
    val contests = _allContests.combine(selectedSite) { contests, site ->
        contests.filter { it.site == site.title }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private fun clearAllContests() {
        _allContests.update { emptyList() }
    }

    private fun addContestToAllContests(contest: CompetraceContest) {
        _allContests.update { it + contest }
    }

    fun getContestListFromKontests() {
        viewModelScope.launch(Dispatchers.IO) {
            kontestsRepository.getAllContests()
                .onStart {
                    _responseForKontestsContestList.update { ApiState.Loading }
                    _isKontestsContestListRefreshing.update { true }
                }
                .catch {
                    _responseForKontestsContestList.update { ApiState.Failure }
                    _isKontestsContestListRefreshing.update { false }
                    Log.e(TAG, "getContestListFromKontests: ${it.cause}")
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

                    _responseForKontestsContestList.update { ApiState.Success }
                    _isKontestsContestListRefreshing.update { false }
                    Log.d(TAG, "Kontests - Got - Contest List")
                }
        }
    }


    fun toggleContestNotification(contest: CompetraceContest) {
        val contestTimeRemInMillis = contest.startTimeInMillis - TimeUtils.currentTimeInMillis()

        if (contest.id.toString() in notificationContestIdList.value) {
            val item = contest.getAlarmItem(scheduleNotifBefore.value)

            viewModelScope.launch {
                contestAlarmRepository.deleteAlarm(item)
                item.let(contestAlarmScheduler::cancel)
            }
            _notificationContestIdList.update { it - contest.id.toString() }
        } else {

            if (contestTimeRemInMillis > TimeUtils.minutesToMillis(scheduleNotifBefore.value)) {
                val item = contest.getAlarmItem(scheduleNotifBefore.value)

                viewModelScope.launch {
                    contestAlarmRepository.addAlarm(item)
                    item.let(contestAlarmScheduler::schedule)
                }
                _notificationContestIdList.update { it + contest.id.toString() }

                SnackbarManager.showMessage(
                    message = UiText.StringResource(
                        R.string.conf_notif_set,
                        ScheduleNotifBeforeOptions.getOption(scheduleNotifBefore.value)
                    )
                )
            } else if (contestTimeRemInMillis > TimeUtils.minutesToMillis(10)) {
                val item = contest.getAlarmItem(10)

                SnackbarManager.showMessageWithAction(
                    messageTextId = UiText.StringResource(R.string.ask_to_set_notif),
                    actionLabelId = UiText.StringResource(R.string.yes),
                    action = {
                        viewModelScope.launch {
                            contestAlarmRepository.addAlarm(item)
                            item.let(contestAlarmScheduler::schedule)
                        }
                        _notificationContestIdList.update { it + contest.id.toString() }
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
                    _notificationContestIdList.update { it - alarm.contestId }
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
                        _notificationContestIdList.update { it + alarm.contestId }
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
