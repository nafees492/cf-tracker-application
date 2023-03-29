package com.gourav.competrace.contests.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.R
import com.gourav.competrace.app_core.AlarmItem
import com.gourav.competrace.app_core.AlarmScheduler
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.KontestsRepository
import com.gourav.competrace.app_core.room_database.AlarmRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.app_core.util.ContestRatedCategories
import com.gourav.competrace.app_core.util.SnackbarManager
import com.gourav.competrace.app_core.util.getCurrentTimeInMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestViewModel @Inject constructor(
    private val kontestsRepository: KontestsRepository,
    private val userPreferences: UserPreferences,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
) : ViewModel() {

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
        ContestSites.values()[it]
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ContestSites.Codeforces
    )

    private val _notificationContestIdList = MutableStateFlow(setOf<String>())
    val notificationContestIdList = _notificationContestIdList.asStateFlow()

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
                                ContestSites.values().any { it.title == site }
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

                        alarmRepository.updateAlarm(
                            AlarmItem(
                                id = contest.uniqueId(),
                                contestId = contest.id.toString(),
                                timeInMillis = contest.startTimeInMillis - 60 * 60 * 1000,
                                title = contest.site ?: "Competrace",
                                message = "${contest.name} is going to start in 60 minutes. Hurry Up!!"
                            )
                        )
                    }

                    _responseForKontestsContestList.update { ApiState.Success }
                    _isKontestsContestListRefreshing.update { false }
                    Log.d(TAG, "Kontests - Got - Contest List")
                }
        }
    }


    fun toggleContestNotification(contest: CompetraceContest) {
        val item = AlarmItem(
            id = contest.uniqueId(),
            contestId = contest.id.toString(),
            timeInMillis = contest.startTimeInMillis - 60 * 60 * 1000,
            title = contest.site ?: "Competrace",
            message = "${contest.name} is going to start within 60 minutes. Hurry Up!!"
        )

        if (contest.id.toString() in notificationContestIdList.value) {
            viewModelScope.launch {
                alarmRepository.deleteAlarm(item)
                item.let(alarmScheduler::cancel)
            }
            _notificationContestIdList.update { it - contest.id.toString() }
        } else {
            viewModelScope.launch {
                alarmRepository.addAlarm(item)
                item.let(alarmScheduler::schedule)
            }
            _notificationContestIdList.update { it + contest.id.toString() }
            SnackbarManager.showMessage(messageTextId = R.string.notification_set_confirmation)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            alarmRepository.getAllAlarms().collect { list ->
                list.forEach { alarm ->
                    alarmScheduler.cancel(alarm)
                    _notificationContestIdList.update { it - alarm.contestId }
                }
            }
            alarmRepository.deleteALlAlarms()
        }

        SnackbarManager.showMessage(R.string.all_notification_cleared)
    }

    init {
        getContestListFromKontests()

        viewModelScope.launch {
            val alarmsToDelete = mutableListOf<AlarmItem>()

            alarmRepository.getAllAlarms().collect { list ->
                list.forEach { alarm ->
                    if (alarm.timeInMillis > getCurrentTimeInMillis()) {
                        alarmScheduler.schedule(alarm)
                        _notificationContestIdList.update { it + alarm.contestId }
                    } else alarmsToDelete.add(alarm)
                }
            }

            alarmsToDelete.forEach {
                alarmRepository.deleteAlarm(it)
            }
        }
    }

    companion object {
        private const val TAG = "Contest ViewModel"
    }
}

enum class ContestSites(val title: String) {
    Codeforces(title = "CodeForces"),
    CodeChef(title = "CodeChef"),
    AtCoder(title = "AtCoder"),
    LeetCode(title = "LeetCode")
}
