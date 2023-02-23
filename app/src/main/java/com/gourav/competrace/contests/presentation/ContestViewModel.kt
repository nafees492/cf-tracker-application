package com.gourav.competrace.contests.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.repository.KontestsRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.utils.ContestRatedCategories
import com.gourav.competrace.utils.Phase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContestViewModel @Inject constructor(
    private val kontestsRepository: KontestsRepository
) : ViewModel() {

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex = _selectedIndex.asStateFlow()

    fun setSelectedIndexTo(value: Int){
        _selectedIndex.update { value }
    }

    var responseForKontestsContestList by mutableStateOf<ApiState>(ApiState.Empty)

    private val _isKontestsContestListRefreshing = MutableStateFlow(false)
    val isKontestsContestListRefreshing = _isKontestsContestListRefreshing.asStateFlow()

    fun refreshContestListFromKontests() = viewModelScope.launch {
        _isKontestsContestListRefreshing.update { true }
        getContestListFromKontests()
    }

    private val kontestsContestListsBySiteFlow =
        mapOf<Pair<String, String>, MutableStateFlow<MutableList<CompetraceContest>>>(
            ContestSites.Codeforces.title to Phase.CODING to MutableStateFlow(mutableListOf()),
            ContestSites.Codeforces.title to Phase.BEFORE to MutableStateFlow(mutableListOf()),

            ContestSites.CodeChef.title to Phase.CODING to MutableStateFlow(mutableListOf()),
            ContestSites.CodeChef.title to Phase.BEFORE to MutableStateFlow(mutableListOf()),

            ContestSites.AtCoder.title to Phase.CODING to MutableStateFlow(mutableListOf()),
            ContestSites.AtCoder.title to Phase.BEFORE to MutableStateFlow(mutableListOf()),

            ContestSites.LeetCode.title to Phase.CODING to MutableStateFlow(mutableListOf()),
            ContestSites.LeetCode.title to Phase.BEFORE to MutableStateFlow(mutableListOf()),

            ContestSites.KickStart.title to Phase.CODING to MutableStateFlow(mutableListOf()),
            ContestSites.KickStart.title to Phase.BEFORE to MutableStateFlow(mutableListOf()),
        )

    private fun clearAllContestsFromKontestsContestListBySite() {
        kontestsContestListsBySiteFlow.values.forEach {
            it.value.clear()
        }
    }

    private fun addContestToKontestsContestListBySite(contest: CompetraceContest) {
        kontestsContestListsBySiteFlow[contest.site to contest.phase]?.value?.add(contest)
    }

    val codeforcesOnGoingContests =
        kontestsContestListsBySiteFlow[ContestSites.Codeforces.title to Phase.CODING]!!.asStateFlow()
    val codeforcesUpComingContests =
        kontestsContestListsBySiteFlow[ContestSites.Codeforces.title to Phase.BEFORE]!!.asStateFlow()

    val codeChefOnGoingContests =
        kontestsContestListsBySiteFlow[ContestSites.CodeChef.title to Phase.CODING]!!.asStateFlow()
    val codeChefUpComingContests =
        kontestsContestListsBySiteFlow[ContestSites.CodeChef.title to Phase.BEFORE]!!.asStateFlow()

    val atCoderOnGoingContests =
        kontestsContestListsBySiteFlow[ContestSites.AtCoder.title to Phase.CODING]!!.asStateFlow()
    val atCoderUpComingContests =
        kontestsContestListsBySiteFlow[ContestSites.AtCoder.title to Phase.BEFORE]!!.asStateFlow()

    val leetCodeOnGoingContests =
        kontestsContestListsBySiteFlow[ContestSites.LeetCode.title to Phase.CODING]!!.asStateFlow()
    val leetCodeUpComingContests =
        kontestsContestListsBySiteFlow[ContestSites.LeetCode.title to Phase.BEFORE]!!.asStateFlow()

    val kickStartOnGoingContests =
        kontestsContestListsBySiteFlow[ContestSites.KickStart.title to Phase.CODING]!!.asStateFlow()
    val kickStartUpComingContests =
        kontestsContestListsBySiteFlow[ContestSites.KickStart.title to Phase.BEFORE]!!.asStateFlow()

    private fun getContestListFromKontests() {
        viewModelScope.launch {
            kontestsRepository.getAllContests()
                .onStart {
                    responseForKontestsContestList = ApiState.Loading
                }
                .catch {
                    responseForKontestsContestList = ApiState.Failure
                    _isKontestsContestListRefreshing.update { false }
                    Log.e(TAG, "getContestListFromKontests: ${it.cause}")
                }
                .collect { apiResult ->
                    clearAllContestsFromKontestsContestListBySite()

                    val contests = apiResult
                        .filter { contest ->
                            contest.site?.let { site ->
                                ContestSites.values().any { it.title == site }
                            } ?: false
                        }.map {
                        CompetraceContest(
                            id = 0,
                            name = it.name,
                            phase = it.status,
                            site = it.site,
                            websiteUrl = it.getWebsiteLink(),
                            startTimeInMillis = it.startTimeInMillis(),
                            durationInMillis = it.durationInMillis(),
                            within7Days = it.within7Days(),
                            registrationOpen = it.registrationOpen(),
                            registrationUrl = it.getRegistrationLink()
                        )
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


                    responseForKontestsContestList = ApiState.Success
                    _isKontestsContestListRefreshing.update { false }
                    Log.d(TAG, "Kontests - Got - Contest List")
                }
        }
    }

    init {
        getContestListFromKontests()
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
