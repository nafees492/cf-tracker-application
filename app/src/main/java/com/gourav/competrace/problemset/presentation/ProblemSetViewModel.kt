package com.gourav.competrace.problemset.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.remote.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.ErrorEntity
import com.gourav.competrace.app_core.util.Sites
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.problemset.model.CompetraceProblem
import com.gourav.competrace.problemset.model.ProblemSetScreenState
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
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val problemSetSites = Sites.values().filter { it.isProblemSetSite }

    private val codeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase
    private val _codeforcesContest = codeforcesDatabase.codeforcesContestListByIdFlow
    private val _allProblems = codeforcesDatabase.allProblemsFlow

    private var fetchProblemsetJob: Job? = null

    fun refreshProblemSetAndContests() {
        getProblemSetFromCodeforces()
    }

    fun clearProblemsFromCodeforcesDatabase() {
        codeforcesDatabase.clearProblems()
    }

    fun addAllProblemsToCodeforcesDatabase(problem: List<CompetraceProblem>) {
        codeforcesDatabase.addAllProblems(problems = problem)
    }


    private val _showTags = userPreferences.showTagsFlow
    private val _allTags = MutableStateFlow(emptyList<String>())

    fun updateAllTags(value: List<String>) {
        _allTags.update { value }
    }


    private val _ratingRangeValue = MutableStateFlow(800..3500)

    fun updateRatingRange(start: Int, end: Int) {
        _ratingRangeValue.update { start..end }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun updateSearchQuery(value: String) {
        _searchQuery.update { value }
    }

    private val _selectedChips = MutableStateFlow(setOf<String>())
    private fun isTagSelected(value: String) = _selectedChips.value.contains(value)

    fun updateSelectedTags(value: String) {
        if (isTagSelected(value)) _selectedChips.update { it - value }
        else _selectedChips.update { it + value }
    }

    fun clearSelectedTags() {
        _selectedChips.update { emptySet() }
    }

    private val _filteredProblems = _allProblems
        .combine(_searchQuery) { problems, searchQuery ->
            if (searchQuery.isBlank()) problems
            else {
                problems.filter {
                    val isProblemMatched = it.name.contains(searchQuery, ignoreCase = true)
                    val isContestMatched = _codeforcesContest.value[it.contestId ?: 0]?.name
                        .toString().contains(searchQuery, ignoreCase = true)
                    searchQuery.isBlank() || isProblemMatched || isContestMatched
                }
            }
        }
        .combine(_ratingRangeValue) { problems, ratingRange ->
            problems.filter {
                val isDefault =
                    (it.rating == null && ratingRange.first == 800 && ratingRange.last == 3500)
                isDefault || (it.rating in ratingRange)
            }
        }
        .combine(_selectedChips) { problems, selectedChips ->
            problems.filter {
                selectedChips.isEmpty() || it.tags?.containsAll(selectedChips) ?: false
            }
        }

    private val _screenState = MutableStateFlow(ProblemSetScreenState())
    val screenState = _screenState
        .combine(_showTags) { state, showTags ->
            state.copy(isTagsVisible = showTags)
        }
        .combine(_allTags) { state, tags ->
            state.copy(allTags = tags)
        }
        .combine(_ratingRangeValue) { state, ratingRange ->
            state.copy(ratingRangeValue = ratingRange)
        }
        .combine(_selectedChips) { state, chips ->
            state.copy(selectedTags = chips)
        }
        .combine(_filteredProblems) { state, problems ->
            state.copy(problems = problems)
        }
        .combine(_codeforcesContest) { state, codeforcesContests ->
            state.copy(codeforcesContests = codeforcesContests)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ProblemSetScreenState()
        )

    private fun getProblemSetFromCodeforces() {
        fetchProblemsetJob?.cancel()
        fetchProblemsetJob = viewModelScope.launch(Dispatchers.IO) {
            codeforcesRepository.getProblemSet()
                .onStart {
                    _screenState.update { it.copy(apiState = ApiState.Loading) }
                }.catch { e ->
                    val errorMessage = ErrorEntity.getError(e).messageId
                    _screenState.update {
                        it.copy(apiState = ApiState.Failure(UiText.StringResource(errorMessage)))
                    }
                    Log.e(TAG, "getProblemSet: ${e.cause}")
                }.collect { apiResult ->
                    if (apiResult.status == "OK") {
                        processCodeforcesProblemSetFromAPIResult(apiResult = apiResult)
                        Log.d(TAG, "Got - Problem Set")
                        _screenState.update { it.copy(apiState = ApiState.Success) }
                    } else {
                        Log.e(TAG, "getProblemSet: " + apiResult.comment.toString())
                        _screenState.update {
                            it.copy(apiState = ApiState.Failure(UiText.DynamicString(apiResult.comment.toString())))
                        }
                    }
                }
        }
    }

    init {
        refreshProblemSetAndContests()
    }

    companion object {
        private const val TAG = "Problem Set ViewModel"
    }
}