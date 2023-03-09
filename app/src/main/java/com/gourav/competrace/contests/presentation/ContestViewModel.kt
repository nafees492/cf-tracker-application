package com.gourav.competrace.contests.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.KontestsRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.app_core.util.ContestRatedCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestViewModel @Inject constructor(
    private val kontestsRepository: KontestsRepository,
    private val userPreferences: UserPreferences
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

    private val _responseForKontestsContestList = MutableStateFlow<ApiState>(ApiState.Loading)
    val responseForKontestsContestList = _responseForKontestsContestList.asStateFlow()

    private val _isKontestsContestListRefreshing = MutableStateFlow(false)
    val isKontestsContestListRefreshing = _isKontestsContestListRefreshing.asStateFlow()

    fun refreshContestListFromKontests() {
        _isKontestsContestListRefreshing.update { true }
        getContestListFromKontests()
    }

    private val contestListsBySiteFlow: Map<String, MutableStateFlow<List<CompetraceContest>>> =
        mapOf(
            ContestSites.Codeforces.title to MutableStateFlow(emptyList()),
            ContestSites.CodeChef.title to MutableStateFlow(emptyList()),
            ContestSites.AtCoder.title to MutableStateFlow(emptyList()),
            ContestSites.LeetCode.title to MutableStateFlow(emptyList()),
        )

    private fun clearAllContestsFromContestListBySite() {
        contestListsBySiteFlow.values.forEach {
            it.update { emptyList() }
        }
    }

    private fun addContestToContestListBySite(contest: CompetraceContest) {
        contestListsBySiteFlow[contest.site]?.update { it + contest }
    }

    val contests: StateFlow<List<CompetraceContest>> = combine(
        selectedSite,
        contestListsBySiteFlow[ContestSites.Codeforces.title]!!,
        contestListsBySiteFlow[ContestSites.CodeChef.title]!!,
        contestListsBySiteFlow[ContestSites.AtCoder.title]!!,
        contestListsBySiteFlow[ContestSites.LeetCode.title]!!
    ) { site, CodeforcesContests, CodeChefContests, AtCoderContests, LeetCodeContests ->
        when (site) {
            ContestSites.Codeforces -> CodeforcesContests
            ContestSites.CodeChef -> CodeChefContests
            ContestSites.AtCoder -> AtCoderContests
            ContestSites.LeetCode -> LeetCodeContests
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
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
                    clearAllContestsFromContestListBySite()

                    val contests = apiResult
                        .filter { contest ->
                            contest.site?.let { site ->
                                ContestSites.values().any { it.title == site }
                            } ?: false
                        }
                        .map {
                            it.mapToCompetraceContest()
                        }.sortedBy {
                            it.startTimeInMillis
                        }

                    contests.forEach { contest ->
                        contest.ratedCategories.clear()
                        ContestRatedCategories.values().forEach {
                            if (contest.name.contains(it.value)) contest.ratedCategories.add(it)
                        }
                        addContestToContestListBySite(contest = contest)
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
    LeetCode(title = "LeetCode")
}
