package com.gourav.competrace.progress.user_submissions.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.problemset.model.CodeforcesProblem
import com.gourav.competrace.progress.user_submissions.model.Submission
import com.gourav.competrace.progress.user_submissions.util.processUserSubmissionsFromAPIResult
import com.gourav.competrace.utils.UserSubmissionFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSubmissionsViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository
) : ViewModel() {

    private var codeforcesDatabase: CodeforcesDatabase =
        CodeforcesDatabase.instance as CodeforcesDatabase

    val contestListById = codeforcesDatabase.codeforcesContestListByIdFlow.asStateFlow()

    private val _currentSelection = MutableStateFlow(UserSubmissionFilter.ALL)
    val currentSelection = _currentSelection.asStateFlow()

    fun updateCurrentSelection(value: String) {
        _currentSelection.update { value }
    }

    var responseForUserSubmissions by mutableStateOf<ApiState>(ApiState.Empty)

    val submittedProblemsFlow =
        MutableStateFlow(arrayListOf<Pair<CodeforcesProblem, ArrayList<Submission>>>())

    val correctProblems = arrayListOf<Pair<CodeforcesProblem, ArrayList<Submission>>>()
    val incorrectProblems = arrayListOf<Pair<CodeforcesProblem, ArrayList<Submission>>>()

    private val _requestedForUserSubmission = MutableStateFlow(false)
    private val requestedForUserSubmission = _requestedForUserSubmission.asStateFlow()

    fun requestForUserSubmission(userPreferences: UserPreferences, isForced: Boolean) {
        if (isForced || !requestedForUserSubmission.value) {
            _requestedForUserSubmission.update { true }
            viewModelScope.launch(Dispatchers.IO) {
                userPreferences.handleNameFlow.collect { handle ->
                    refreshUserSubmission(handle!!)
                }
            }
        }
    }

    private val _isUserSubmissionRefreshing = MutableStateFlow(false)
    val isUserSubmissionRefreshing = _isUserSubmissionRefreshing.asStateFlow()

    private fun refreshUserSubmission(handle: String) = viewModelScope.launch {
        _isUserSubmissionRefreshing.update { true }
        // Simulate API call
        getUserSubmission(handle = handle)
    }

    private fun getUserSubmission(handle: String) {
        viewModelScope.launch {
            codeforcesRepository.getUserSubmissions(handle = handle)
                .onStart {
                    responseForUserSubmissions = ApiState.Loading
                }.catch {
                    responseForUserSubmissions = ApiState.Failure
                    _isUserSubmissionRefreshing.update { false }
                    Log.e(TAG, "getUserSubmission: $it")
                }.collect {
                    if (it.status == "OK") {

                        processUserSubmissionsFromAPIResult(codeforcesApiResult = it)

                        responseForUserSubmissions = ApiState.Success
                        Log.d(TAG, "Got - User Submissions")
                    } else {
                        responseForUserSubmissions = ApiState.Failure
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
            when(currentSelection){
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
            SharingStarted.WhileSubscribed(5_000),
            submittedProblemsFlow.value
        )

    companion object {
        private const val TAG = "User Submission ViewModel"
    }
}