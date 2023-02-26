package com.gourav.competrace.progress.participated_contests.presentation

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
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.progress.participated_contests.model.UserRatingChanges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipatedContestViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository
) : ViewModel() {
    private var codeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    private val contestListById = codeforcesDatabase.codeforcesContestListByIdFlow.asStateFlow()

    private val _requestedForUserRatingChanges = MutableStateFlow(false)
    private val requestedForUserRatingChanges = _requestedForUserRatingChanges.asStateFlow()

    private val _participatedContests = MutableStateFlow(listOf<CompetraceContest>())
    val participatedContests = _participatedContests.asStateFlow()

    private val _isUserRatingChangesRefreshing = MutableStateFlow(false)
    val isUserRatingChangesRefreshing = _isUserRatingChangesRefreshing.asStateFlow()

    fun requestForUserRatingChanges(userPreferences: UserPreferences, isForced: Boolean) {
        if (isForced || !requestedForUserRatingChanges.value) {
            _requestedForUserRatingChanges.update { true }
            viewModelScope.launch {
                userPreferences.handleNameFlow.collect { handle ->
                    getUserRatingChanges(handle)
                }
            }
        }
    }

    var responseForUserRatingChanges by mutableStateOf<ApiState>(ApiState.Empty)
    private fun getUserRatingChanges(handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            codeforcesRepository.getUserRatingChanges(handle = handle)
                .onStart {
                    responseForUserRatingChanges = ApiState.Loading
                    _isUserRatingChangesRefreshing.update { true }
                }.catch {
                    responseForUserRatingChanges = ApiState.Failure
                    _isUserRatingChangesRefreshing.update { false }
                    Log.e(TAG, "getUserRatingChanges: $it")
                }.collect { apiResult ->
                    if (apiResult.status == "OK") {
                        val userRatingChanges = apiResult.result as List<UserRatingChanges>
                        val codeforcesContests = mutableListOf<CompetraceContest>()

                        userRatingChanges.forEach {
                            contestListById.value[it.contestId]?.apply {
                                ratingChange = it.newRating - it.oldRating
                                newRating = it.newRating
                                rank = it.rank
                            }?.also { contest ->
                                codeforcesContests.add(contest)
                            }
                        }
                        _participatedContests.update { codeforcesContests }

                        responseForUserRatingChanges = ApiState.Success
                        Log.d(TAG, "Got - User Rating Changes")
                    } else {
                        responseForUserRatingChanges = ApiState.Failure
                        Log.e(TAG, "getUserRatingChanges: " + apiResult.comment.toString())
                    }
                    _isUserRatingChangesRefreshing.update { false }
                }
        }
    }

    companion object {
        private const val TAG = "Finished Contest ViewModel"
    }
}