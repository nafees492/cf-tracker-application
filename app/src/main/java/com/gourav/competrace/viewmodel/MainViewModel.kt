package com.gourav.competrace.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.model.Contest
import com.gourav.competrace.model.Problem
import com.gourav.competrace.model.Submission
import com.gourav.competrace.model.User
import com.gourav.competrace.retrofit.repository.MainRepository
import com.gourav.competrace.retrofit.util.ApiState
import com.gourav.competrace.utils.Phase
import com.gourav.competrace.utils.processContestFromAPIResult
import com.gourav.competrace.utils.processSubmittedProblemFromAPIResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    private val _isSplashScreenLoading = MutableStateFlow(true)
    val isSplashScreenLoading = _isSplashScreenLoading.asStateFlow()

    init {
        getContestList()
        getProblemSet()
        viewModelScope.launch {
            delay(1000)
            _isSplashScreenLoading.value = false
        }
    }

    var user by mutableStateOf<User?>(null)

    var responseForUserInfo by mutableStateOf<ApiState>(ApiState.Empty)

    private val _requestedForUserInfo = MutableStateFlow(false)
    private val requestedForUserInfo = _requestedForUserInfo.asStateFlow()

    fun requestForUserInfo(userPreferences: UserPreferences, isForced: Boolean) {
        if (isForced || !requestedForUserInfo.value) {
            _requestedForUserInfo.update { true }
            viewModelScope.launch(Dispatchers.IO) {
                userPreferences.handleNameFlow.collect { handle ->
                    refreshUser(handle!!)
                }
            }
        }
    }

    private val _isUserRefreshing = MutableStateFlow(false)
    val isUserRefreshing = _isUserRefreshing.asStateFlow()

    private fun refreshUser(handle: String) = viewModelScope.launch {
        _isUserRefreshing.update { true }
        // Simulate API call
        getUserInfo(handle = handle)
    }

    fun getUserInfo(handle: String) {
        viewModelScope.launch {
            mainRepository.getUserInfo(handle = handle)
                .onStart {
                    responseForUserInfo = ApiState.Loading
                }.catch {
                    responseForUserInfo = ApiState.Failure(it)
                    _isUserRefreshing.update { false }
                    Log.e(TAG, it.toString())
                }.collect {
                    responseForUserInfo = ApiState.Success(it)
                    _isUserRefreshing.update { false }
                    Log.d(TAG, "Got - User Info")
                }
        }
    }

    val contestListsByPhase = mapOf<String, MutableList<Contest>>(
        Phase.CODING to mutableListOf(),
        Phase.BEFORE to mutableListOf(),
        Phase.FINISHED to mutableListOf(),
        Phase.PENDING_SYSTEM_TEST to mutableListOf(),
        Phase.SYSTEM_TEST to mutableListOf()
    )
    val contestListBeforeByPhase = mapOf<String, MutableList<Contest>>(
        Phase.WITHIN_7DAYS to mutableListOf(),
        Phase.AFTER_7DAYS to mutableListOf(),
    )

    val contestListById = mutableMapOf<Int, Contest>()
    var responseForContestList by mutableStateOf<ApiState>(ApiState.Empty)

    private val _isContestListRefreshing = MutableStateFlow(false)
    val isContestListRefreshing = _isContestListRefreshing.asStateFlow()

    fun refreshContestList() = viewModelScope.launch {
        _isContestListRefreshing.update { true }
        // Simulate API call
        getContestList()
    }
    fun getContestList() {
        viewModelScope.launch {
            delay(1000)
            // Normal Contests.
            mainRepository.getContestList(gym = false)
                .onStart {
                    responseForContestList = ApiState.Loading
                }.catch {
                    responseForContestList = ApiState.Failure(it)
                    _isContestListRefreshing.update { false }
                    Log.e(TAG, it.toString())
                }.collect {

                    val apiResult = ApiState.Success(it)
                    processContestFromAPIResult(
                        apiResult = apiResult,
                        mainViewModel = this@MainViewModel
                    )
                    responseForContestList = apiResult

                    _isContestListRefreshing.update { false }
                    Log.d(TAG, "Got - Contest List")
                }
            delay(1000)
            // Gym Contests.
            mainRepository.getContestList(gym = true)
                .collect {
                    if(it.status == "OK"){
                        val contestList = it.result
                        contestList?.forEach{ contest ->
                            contestListById[contest.id] = contest
                        }
                    }
                    Log.d(TAG, "Got - Gym Contest List")
                }
        }
    }

    var responseForProblemSet by mutableStateOf<ApiState>(ApiState.Empty)
    val tagList = arrayListOf<String>()

    private val _isProblemSetRefreshing = MutableStateFlow(false)
    val isProblemSetRefreshing = _isProblemSetRefreshing.asStateFlow()

    fun refreshProblemSet() = viewModelScope.launch {
        _isProblemSetRefreshing.update { true }
        // Simulate API call
        getProblemSet()
    }

    fun getProblemSet() {
        viewModelScope.launch {
            delay(1000)
            mainRepository.getProblemSet()
                .onStart {
                    responseForProblemSet = ApiState.Loading
                }.catch {
                    responseForProblemSet = ApiState.Failure(it)
                    _isProblemSetRefreshing.update { false }
                    Log.e(TAG, it.toString())
                }.collect {
                    responseForProblemSet = ApiState.SuccessPS(it)
                    _isProblemSetRefreshing.update { false }
                    Log.d(TAG, "Got - Problem Set")
                }
        }
    }

    var responseForUserSubmissions by mutableStateOf<ApiState>(ApiState.Empty)
    val submittedProblems = arrayListOf<Pair<Problem, ArrayList<Submission>>>()
    val correctProblems = arrayListOf<Pair<Problem, ArrayList<Submission>>>()
    val incorrectProblems = arrayListOf<Pair<Problem, ArrayList<Submission>>>()

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
            mainRepository.getUserSubmissions(handle = handle)
                .onStart {
                    responseForUserSubmissions = ApiState.Loading
                }.catch {
                    responseForUserSubmissions = ApiState.Failure(it)
                    _isUserSubmissionRefreshing.update { false }
                    Log.e(TAG, it.toString())
                }.collect {

                    val apiResult = ApiState.Success(it)
                    processSubmittedProblemFromAPIResult(
                        mainViewModel = this@MainViewModel,
                        apiResult = apiResult
                    )
                    responseForUserSubmissions = apiResult

                    _isUserSubmissionRefreshing.update { false }
                    Log.d(TAG, "Got - User Submissions")
                }
        }
    }

    private val _requestedForUserRatingChanges = MutableStateFlow(false)
    private val requestedForUserRatingChanges = _requestedForUserRatingChanges.asStateFlow()

    fun requestForUserRatingChanges(userPreferences: UserPreferences, isForced: Boolean) {
        if (isForced || !requestedForUserRatingChanges.value) {
            _requestedForUserRatingChanges.update { true }
            viewModelScope.launch(Dispatchers.IO) {
                userPreferences.handleNameFlow.collect { handle ->
                    getUserRatingChanges(handle!!)
                }
            }
        }
    }

    var responseForUserRatingChanges by mutableStateOf<ApiState>(ApiState.Empty)
    private fun getUserRatingChanges(handle: String){
        viewModelScope.launch {
            mainRepository.getUserRatingChanges(handle = handle)
                .onStart {
                    responseForUserRatingChanges = ApiState.Loading
                }.catch {
                    responseForUserRatingChanges = ApiState.Failure(it)
                    Log.e(TAG, it.toString())
                }.collect {
                    responseForUserRatingChanges = ApiState.Success(it)
                    Log.d(TAG, "Got - User Rating Changes")
                }
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}