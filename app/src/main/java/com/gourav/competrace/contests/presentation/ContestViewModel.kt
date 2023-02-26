package com.gourav.competrace.contests.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.repository.KontestsRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.utils.ContestRatedCategories
import com.gourav.competrace.utils.Phase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestViewModel @Inject constructor(
    private val kontestsRepository: KontestsRepository,
) : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    fun setSelectedIndexTo(value: Int) {
        _selectedIndex.update { value }
    }

    private val currentScreen = selectedIndex.map {
        ContestSites.values()[it]
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ContestSites.Codeforces
    )

    private val _responseForKontestsContestList = MutableStateFlow<ApiState>(ApiState.Loading)
    val responseForKontestsContestList = _responseForKontestsContestList.asStateFlow()

    private val _isKontestsContestListRefreshing = MutableStateFlow(false)
    val isKontestsContestListRefreshing = _isKontestsContestListRefreshing.asStateFlow()

    fun refreshContestListFromKontests() {
        _isKontestsContestListRefreshing.update { true }
        getContestListFromKontests()
    }

    private val kontestsContestListsBySiteFlow: Map<String, MutableStateFlow<MutableList<CompetraceContest>>> =
        mapOf(
            ContestSites.Codeforces.title to MutableStateFlow(mutableListOf()),
            ContestSites.CodeChef.title to MutableStateFlow(mutableListOf()),
            ContestSites.AtCoder.title to MutableStateFlow(mutableListOf()),
            ContestSites.LeetCode.title to MutableStateFlow(mutableListOf()),
            ContestSites.KickStart.title to MutableStateFlow(mutableListOf()),
        )

    private fun clearAllContestsFromKontestsContestListBySite() {
        kontestsContestListsBySiteFlow.values.forEach {
            it.value.clear()
        }
    }

    private fun addContestToKontestsContestListBySite(contest: CompetraceContest) {
        kontestsContestListsBySiteFlow[contest.site]?.value?.add(contest)
    }

    val currentContests = currentScreen.map { site ->
        kontestsContestListsBySiteFlow[site.title]!!.value
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        kontestsContestListsBySiteFlow[ContestSites.Codeforces.title]!!.value
    )

    private fun getContestListFromKontests() {
        viewModelScope.launch(Dispatchers.IO) {
            kontestsRepository.getAllContests()
                .onStart {
                    _responseForKontestsContestList.update { ApiState.Loading }
                }
                .catch {
                    _responseForKontestsContestList.update { ApiState.Failure }
                    _isKontestsContestListRefreshing.update { false }
                    Log.e(TAG, "getContestListFromKontests: ${it.cause}")
                }
                .collect { apiResult ->
                    clearAllContestsFromKontestsContestListBySite()

                    val contests = apiResult
                        .map {
                            it.mapToCompetraceContest()
                        }
                        .filter { contest ->
                            contest.site?.let { site ->
                                ContestSites.values().any { it.title == site }
                            } ?: false
                        }.sortedBy {
                            it.startTimeInMillis
                        }

                    contests.forEach { contest ->
                        contest.ratedCategories.clear()
                        ContestRatedCategories.values().forEach {
                            if (contest.name.contains(it.value)) contest.ratedCategories.add(it)
                        }
                        addContestToKontestsContestListBySite(contest = contest)
                    }

                    _responseForKontestsContestList.update { ApiState.Success }
                    _isKontestsContestListRefreshing.update { false }
                    Log.d(TAG, "Kontests - Got - Contest List")
                }
        }
    }

    init {
        refreshContestListFromKontests()
    }

    companion object {
        private const val TAG = "Contest ViewModel"
    }
}

enum class ContestSites(val title: String) {
    Codeforces(title = "CodeForces"),
    CodeChef(title = "CodeChef"),
    AtCoder(title = "AtCoder"),
    LeetCode(title = "LeetCode"),
    KickStart(title = "Kick Start")
}
