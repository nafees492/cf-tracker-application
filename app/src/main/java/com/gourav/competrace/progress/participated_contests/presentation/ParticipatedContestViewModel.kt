package com.gourav.competrace.progress.participated_contests.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.CodeforcesDatabase
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.remote.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.ErrorEntity
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.contests.model.CompetraceContest
import com.gourav.competrace.progress.participated_contests.model.UserRatingChanges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipatedContestViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val codeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    private val contestListById = codeforcesDatabase.codeforcesContestListByIdFlow.asStateFlow()

    private val _userRatingChanges = MutableStateFlow(listOf<UserRatingChanges>())

    val participatedContests =
        combine(_userRatingChanges, contestListById) { ratingChanges, contestListById ->
            val contests = mutableListOf<CompetraceContest>()
            ratingChanges.forEach {
                contestListById[it.contestId]?.apply {
                    ratingChange = it.newRating - it.oldRating
                    newRating = it.newRating
                    rank = it.rank
                }?.also { contest ->
                    contests.add(contest)
                }
            }
            contests
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val _isUserRatingChangesRefreshing = MutableStateFlow(false)
    val isUserRatingChangesRefreshing = _isUserRatingChangesRefreshing.asStateFlow()
    
    fun refreshUserRatingChanges() {
        viewModelScope.launch {
            userPreferences.handleNameFlow.collect {
                if (it.isNotBlank()) getUserRatingChanges(it)
            }
        }
    }

    private val _responseForUserRatingChanges = MutableStateFlow<ApiState>(ApiState.Loading)
    val responseForUserRatingChanges = _responseForUserRatingChanges.asStateFlow()

    private fun getUserRatingChanges(handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            codeforcesRepository.getUserRatingChanges(handle = handle)
                .onStart {
                    _responseForUserRatingChanges.update { ApiState.Loading }
                    _isUserRatingChangesRefreshing.update { true }
                }.catch { e ->
                    val errorMessage = ErrorEntity.getError(e).messageId
                    _responseForUserRatingChanges.update {
                        ApiState.Failure(
                            UiText.StringResource(
                                errorMessage
                            )
                        )
                    }
                    _isUserRatingChangesRefreshing.update { false }
                    Log.e(TAG, "getUserRatingChanges: $e")
                }.collect { apiResult ->
                    if (apiResult.status == "OK") {
                        val userRatingChanges = apiResult.result as List<UserRatingChanges>
                        _userRatingChanges.update { userRatingChanges }

                        _responseForUserRatingChanges.update { ApiState.Success }
                        Log.d(TAG, "Got - User Rating Changes")
                    } else {
                        _responseForUserRatingChanges.update {
                            ApiState.Failure(
                                UiText.DynamicString(
                                    apiResult.comment.toString()
                                )
                            )
                        }
                        Log.e(TAG, "getUserRatingChanges: " + apiResult.comment.toString())
                    }
                    _isUserRatingChangesRefreshing.update { false }
                }
        }
    }

    init {
        refreshUserRatingChanges()
    }

    companion object {
        private const val TAG = "Participated Contest ViewModel"
    }
}