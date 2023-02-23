package com.gourav.competrace.progress.user.presentation

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository
) : ViewModel() {

    private var codeforcesDatabase: CodeforcesDatabase = CodeforcesDatabase.instance as CodeforcesDatabase

    var responseForUserInfo by mutableStateOf<ApiState>(ApiState.Empty)

    private val _requestedForUserInfo = MutableStateFlow(false)
    private val requestedForUserInfo = _requestedForUserInfo.asStateFlow()

    val currentUser = codeforcesDatabase.currentUserFlow.asStateFlow()

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

    private fun getUserInfo(handle: String) {
        viewModelScope.launch {
            codeforcesRepository.getUserInfo(handle = handle)
                .onStart {
                    responseForUserInfo = ApiState.Loading
                }.catch {
                    responseForUserInfo = ApiState.Failure
                    _isUserRefreshing.update { false }
                    Log.e(TAG, "getUserInfo: $it")
                }.collect {
                    if(it.status == "OK"){

                        codeforcesDatabase.setUser(user = it.result!![0])

                        responseForUserInfo = ApiState.Success
                        Log.d(TAG, "Got - User Info - $handle")
                    } else {
                        responseForUserInfo = ApiState.Failure
                        Log.e(TAG, "getUserInfo: " + it.comment.toString())
                    }
                    _isUserRefreshing.update { false }
                }
        }
    }

    companion object{
        private const val TAG = "User ViewModel"
    }
}