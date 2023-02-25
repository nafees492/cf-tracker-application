package com.gourav.competrace.problemset.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.repository.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.contests.util.processCodeforcesContestFromAPIResult
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.problemset.util.processCodeforcesProblemSetFromAPIResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProblemSetViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository
) : ViewModel() {
    private var codeforcesDatabase: CodeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    init {
        getProblemSetFromCodeforces()
        getContestListFromCodeforces()
    }

    fun refreshProblemSetAndContests() {
        viewModelScope.launch {
            _isProblemSetRefreshing.update { true }
            // Simulate API call
            getProblemSetFromCodeforces()
            getContestListFromCodeforces()
        }
    }

    fun addContestToContestListById(codeforcesContest: CompetraceContest) {
        codeforcesDatabase.addContestToContestListById(codeforcesContest = codeforcesContest)
    }

    private fun getContestListFromCodeforces() {
        viewModelScope.launch {
            // Load Normal Contests.
            codeforcesRepository.getContestList(gym = false)
                .onStart {
                }.catch {
                    Log.e(TAG, "getContestListFromCodeforces: ${it.cause}")
                }.collect {
                    if (it.status == "OK") {

                        processCodeforcesContestFromAPIResult(apiResult = it)

                        Log.d(TAG, "Codeforces - Got - Contest List")
                    } else {
                        Log.e(TAG, "getContestListFromCodeforces: " + it.comment.toString())
                    }
                }
            delay(100)
            // Load Gym Contests.
            codeforcesRepository.getContestList(gym = true)
                .catch {
                    Log.e(TAG, "getContestListFromCodeforces: ${it.cause}")
                }
                .collect { apiResult ->
                    if (apiResult.status == "OK") {
                        apiResult.result?.map {
                            CompetraceContest(
                                id = it.id,
                                name = it.name,
                                phase = it.phase,
                                websiteUrl = it.getLink(),
                                startTimeInMillis = it.startTimeInMillis(),
                                durationInMillis = it.durationInMillis(),
                                within7Days = it.within7Days(),
                            )
                        }?.forEach { contest ->
                            codeforcesDatabase.addContestToContestListById(codeforcesContest = contest)
                        }

                        Log.d(TAG, "Codeforces - Got - Gym Contest List")
                    } else {
                        Log.e(TAG, "getContestListFromCodeforces: " + apiResult.comment.toString())
                    }
                }
        }
    }

    val contestListById = codeforcesDatabase.codeforcesContestListByIdFlow.asStateFlow()
    private val allProblems = codeforcesDatabase.allProblemsFlow.asStateFlow()

    fun clearProblemsFromCodeforcesDatabase(){
        codeforcesDatabase.clearProblems()
    }

    fun addAllProblemsToCodeforcesDatabase(codeforcesProblems: List<CodeforcesProblem>){
        codeforcesDatabase.addAllProblems(codeforcesProblems = codeforcesProblems)
    }

    var responseForProblemSet by mutableStateOf<ApiState>(ApiState.Empty)
    val tagList = arrayListOf<String>()

    private val _isProblemSetRefreshing = MutableStateFlow(false)
    val isProblemSetRefreshing = _isProblemSetRefreshing.asStateFlow()

    private fun getProblemSetFromCodeforces() {
        viewModelScope.launch {
            delay(100)
            codeforcesRepository.getProblemSet()
                .onStart {
                    responseForProblemSet = ApiState.Loading
                }.catch {
                    responseForProblemSet = ApiState.Failure
                    _isProblemSetRefreshing.update { false }
                    Log.e(TAG, "getProblemSet: $it")
                }.collect {
                    if(it.status == "OK"){

                        processCodeforcesProblemSetFromAPIResult(apiResult = it)

                        responseForProblemSet = ApiState.Success
                        Log.d(TAG, "Got - Problem Set")
                    } else {
                        responseForProblemSet = ApiState.Failure
                        Log.e(TAG, "getProblemSet: " + it.comment.toString())
                    }
                    _isProblemSetRefreshing.update { false }
                }
        }
    }

    private val _ratingRangeValue = MutableStateFlow(800..3500)
    val ratingRangeValue = _ratingRangeValue.asStateFlow()

    fun updateRatingRange(start: Int, end: Int){
        _ratingRangeValue.update { start..end }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun updateSearchQuery(value: String){
        _searchQuery.update { value }
    }

    val filteredProblems = allProblems
        .combine(searchQuery){ problems, searchQuery ->
            if(searchQuery.isBlank()) problems
            else {
                problems.filter {
                    val isProblemMatched = it.name.contains(searchQuery, ignoreCase = true)
                    val isContestMatched = contestListById.value[it.contestId ?: 0]?.name
                        .toString().contains(searchQuery, ignoreCase = true)
                    searchQuery.isBlank() || isProblemMatched || isContestMatched
                }
            }
        }
        .combine(ratingRangeValue){ problems, ratingRange ->
            problems.filter {
                val isDefault =
                    (it.rating == null && ratingRange.first == 800 && ratingRange.last == 3500)
                isDefault || (it.rating in ratingRange)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            allProblems.value
        )

    companion object {
        private const val TAG = "Problem Set ViewModel"
    }
}