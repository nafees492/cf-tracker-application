package com.gourav.competrace.problemset.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.repository.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.util.processCodeforcesContestFromAPIResult
import com.gourav.competrace.problemset.model.CompetraceProblem
import com.gourav.competrace.problemset.util.processCodeforcesProblemSetFromAPIResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProblemSetViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository
) : ViewModel() {
    private var codeforcesDatabase: CodeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    private var fetchContestJob: Job? = null
    private var fetchProblemsetJob: Job? = null

    fun refreshProblemSetAndContests() {
        _isProblemSetRefreshing.update { true }
        getContestListFromCodeforces()
        getProblemSetFromCodeforces()
    }

    fun addContestToContestListById(codeforcesContest: CompetraceContest) {
        codeforcesDatabase.addContestToContestListById(contest = codeforcesContest)
    }

    private fun getContestListFromCodeforces() {
        fetchContestJob?.cancel()
        fetchContestJob = viewModelScope.launch(Dispatchers.IO) {
            // Load Normal Contests.
            codeforcesRepository.getContestList(gym = false)
                .catch {
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
                            it.mapToCompetraceContest()
                        }?.forEach { contest ->
                            codeforcesDatabase.addContestToContestListById(contest = contest)
                        }

                        Log.d(TAG, "Codeforces - Got - Gym Contest List")
                    } else {
                        Log.e(TAG, "getContestListFromCodeforces: " + apiResult.comment.toString())
                    }
                }
        }
    }

    val codeforcesContestListById = codeforcesDatabase.codeforcesContestListByIdFlow.asStateFlow()
    private val allProblems = codeforcesDatabase.allProblemsFlow.asStateFlow()

    fun clearProblemsFromCodeforcesDatabase(){
        codeforcesDatabase.clearProblems()
    }

    fun addAllProblemsToCodeforcesDatabase(problem: List<CompetraceProblem>){
        codeforcesDatabase.addAllProblems(problems = problem)
    }

    private val _responseForProblemSet = MutableStateFlow<ApiState>(ApiState.Loading)
    val responseForProblemSet = _responseForProblemSet.asStateFlow()

    val tagList = arrayListOf<String>()

    private val _isProblemSetRefreshing = MutableStateFlow(false)
    val isProblemSetRefreshing = _isProblemSetRefreshing.asStateFlow()

    private fun getProblemSetFromCodeforces() {
        fetchProblemsetJob?.cancel()
        fetchProblemsetJob = viewModelScope.launch(Dispatchers.IO) {
            delay(100)
            codeforcesRepository.getProblemSet()
                .onStart {
                    _responseForProblemSet.update { ApiState.Loading }
                }.catch {
                    _responseForProblemSet.update { ApiState.Failure }
                    _isProblemSetRefreshing.update { false }
                    Log.e(TAG, "getProblemSet: ${it.cause}")
                }.collect {
                    if(it.status == "OK"){

                        processCodeforcesProblemSetFromAPIResult(apiResult = it)

                        _responseForProblemSet.update { ApiState.Success }
                        Log.d(TAG, "Got - Problem Set")
                    } else {
                        _responseForProblemSet.update { ApiState.Failure }
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

    private val _selectedChips = MutableStateFlow(setOf<String>())
    val selectedChips = _selectedChips.asStateFlow()

    private fun isChipSelected(value: String) = selectedChips.value.contains(value)

    fun updateSelectedChips(value: String){
        if(isChipSelected(value)) _selectedChips.update { it - value }
        else _selectedChips.update { it + value }
    }

    fun clearSelectedChips(){
        _selectedChips.update { emptySet() }
    }

    val filteredProblems = allProblems
        .combine(searchQuery){ problems, searchQuery ->
            if(searchQuery.isBlank()) problems
            else {
                problems.filter {
                    val isProblemMatched = it.name.contains(searchQuery, ignoreCase = true)
                    val isContestMatched = codeforcesContestListById.value[it.contestId ?: 0]?.name
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
        .combine(selectedChips){ problems, selectedChips ->
            problems.filter {
                selectedChips.isEmpty() || it.tags?.containsAll(selectedChips) ?: false
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            allProblems.value
        )

    init {
        refreshProblemSetAndContests()
    }

    companion object {
        private const val TAG = "Problem Set ViewModel"
    }
}