package com.gourav.competrace.progress.user_submissions.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.remote.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.progress.user_submissions.util.processUserSubmissionsFromAPIResult
import com.gourav.competrace.app_core.util.UserSubmissionFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSubmissionsViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val codeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    val showTags = userPreferences.showTagsFlow

    val contestListById = codeforcesDatabase.codeforcesContestListByIdFlow.asStateFlow()

    private val _currentSelection = MutableStateFlow<UiText>(UserSubmissionFilter.ALL)
    val currentSelection = _currentSelection.asStateFlow()

    fun updateCurrentSelection(value: UiText) {
        _currentSelection.update { value }
    }

    private val _responseForUserSubmission = MutableStateFlow<ApiState>(ApiState.Loading)
    var responseForUserSubmissions = _responseForUserSubmission.asStateFlow()

    val submittedProblemsFlow =
        MutableStateFlow(arrayListOf<Pair<CodeforcesProblem, ArrayList<Submission>>>())

    val correctProblems = arrayListOf<Pair<CodeforcesProblem, ArrayList<Submission>>>()
    val incorrectProblems = arrayListOf<Pair<CodeforcesProblem, ArrayList<Submission>>>()

    fun refreshUserSubmission() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.handleNameFlow.collect {
                if(it.isNotBlank()) getUserSubmission(it)
            }
        }
    }

    private val _isUserSubmissionRefreshing = MutableStateFlow(false)
    val isUserSubmissionRefreshing = _isUserSubmissionRefreshing.asStateFlow()

    private fun getUserSubmission(handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            codeforcesRepository.getUserSubmissions(handle = handle)
                .onStart {
                    _isUserSubmissionRefreshing.update { true }
                    _responseForUserSubmission.update { ApiState.Loading }
                }.catch {
                    _responseForUserSubmission.update { ApiState.Failure }
                    _isUserSubmissionRefreshing.update { false }
                    Log.e(TAG, "getUserSubmission: $it")
                }.collect {
                    if (it.status == "OK") {

                        processUserSubmissionsFromAPIResult(codeforcesApiResult = it)

                        _responseForUserSubmission.update { ApiState.Success }

                        Log.d(TAG, "Got - User Submissions")
                    } else {
                        _responseForUserSubmission.update { ApiState.Failure }
                        Log.e(TAG, "getUserSubmission: " + it.comment.toString())
                    }
                    _isUserSubmissionRefreshing.update { false }
                }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    fun updateSearchQuery(value: String) {
        _searchQuery.update { value }
    }

    val filteredProblemsWithSubmissions = submittedProblemsFlow
        .combine(currentSelection) { problems, currentSelection ->
            when (currentSelection) {
                UserSubmissionFilter.CORRECT -> correctProblems
                UserSubmissionFilter.INCORRECT -> incorrectProblems
                else -> problems
            }
        }
        .combine(searchQuery) { problems, searchQuery ->
            if (searchQuery.isBlank()) problems
            else {
                problems.filter {
                    val isProblemMatched =
                        it.first.name.contains(searchQuery, ignoreCase = true)
                    val isContestMatched =
                        contestListById.value[it.first.contestId ?: 0]?.name
                            .toString().contains(searchQuery, ignoreCase = true)
                    isProblemMatched || isContestMatched
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            submittedProblemsFlow.value
        )

    init {
        refreshUserSubmission()
    }

    companion object {
        private const val TAG = "User Submission ViewModel"
    }
}