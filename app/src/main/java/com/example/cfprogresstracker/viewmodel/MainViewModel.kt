package com.example.cfprogresstracker.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.model.User
import com.example.cfprogresstracker.retrofit.repository.MainRepository
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.utils.Phase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    init {
        getContestList()
        getProblemSet()
    }

    var user by mutableStateOf<User?>(null)

    var responseForUserInfo by mutableStateOf<ApiState>(ApiState.Empty)

    fun getUserInfo(handle: String) {
        viewModelScope.launch {
            mainRepository.getUserInfo(handle = handle)
                .onStart {
                    responseForUserInfo = ApiState.Loading
                }.catch {
                    responseForUserInfo = ApiState.Failure(it)
                    Log.d(TAG, it.toString())
                }.collect {
                    responseForUserInfo = ApiState.Success(it)
                    Log.d(TAG, it.toString())
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
        Phase.WITHIN_2DAYS to mutableListOf(),
        Phase.MORE to mutableListOf(),
    )
    val contestListById = mutableMapOf<Int, Contest>()
    var responseForContestList by mutableStateOf<ApiState>(ApiState.Empty)
    fun getContestList() {
        viewModelScope.launch {
            delay(1000)
            mainRepository.getContestList()
                .onStart {
                    responseForContestList = ApiState.Loading
                }.catch {
                    responseForContestList = ApiState.Failure(it)
                    Log.d(TAG, it.toString())
                }.collect {
                    responseForContestList = ApiState.Success(it)
                    Log.d(TAG, it.toString())
                }
        }
    }

    var responseForProblemSet by mutableStateOf<ApiState>(ApiState.Empty)
    fun getProblemSet() {
        viewModelScope.launch {
            delay(1000)
            mainRepository.getProblemSet()
                .onStart {
                    responseForProblemSet = ApiState.Loading
                }.catch {
                    responseForProblemSet = ApiState.Failure(it)
                    Log.d(TAG, it.toString())
                }.collect {
                    responseForProblemSet = ApiState.SuccessPS(it)
                    Log.d(TAG, it.toString())
                }
        }
    }

    var responseForUserSubmissions by mutableStateOf<ApiState>(ApiState.Empty)
    val submittedProblems = arrayListOf<Pair<Problem, ArrayList<Submission>>>()
    val correctProblems = arrayListOf<Pair<Problem, ArrayList<Submission>>>()
    val incorrectProblems = arrayListOf<Pair<Problem, ArrayList<Submission>>>()


    fun getUserSubmission(userHandle: String) {
        viewModelScope.launch {
            mainRepository.getUserSubmissions(userHandle = userHandle)
                .onStart {
                    responseForUserSubmissions = ApiState.Loading
                }.catch {
                    responseForUserSubmissions = ApiState.Failure(it)
                    Log.d(TAG, it.toString())
                }.collect {
                    responseForUserSubmissions = ApiState.Success(it)
                    Log.d(TAG, it.toString())
                }
        }
    }

    var responseForUserRatingChanges by mutableStateOf<ApiState>(ApiState.Empty)
    fun getUserRatingChanges(handle: String){
        viewModelScope.launch {
            mainRepository.getUserRatingChanges(userHandle = handle)
                .onStart {
                    responseForUserRatingChanges = ApiState.Loading
                }.catch {
                    responseForUserRatingChanges = ApiState.Failure(it)
                    Log.d(TAG, it.toString())
                }.collect {
                    responseForUserRatingChanges = ApiState.Success(it)
                    Log.d(TAG, it.toString())
                }
        }
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}