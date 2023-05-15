package com.gourav.competrace.progress.user.presentation

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userSites = Sites.values().filter { it.isUserSite }

    val userHandle = userPreferences.handleNameFlow
    private val codeforcesDatabase: CodeforcesDatabase =
        CodeforcesDatabase.instance as CodeforcesDatabase

    private val _responseForUserInfo = MutableStateFlow<ApiState>(ApiState.Success)
    val responseForUserInfo = _responseForUserInfo.asStateFlow()

    val currentUser = codeforcesDatabase.currentUserFlow.asStateFlow()

    fun refreshUserInfo() {
        viewModelScope.launch {
            userPreferences.handleNameFlow.collect {
                if (it.isNotBlank()) getUserInfo(it)
            }
        }
    }

    private val _isUserRefreshing = MutableStateFlow(false)
    val isUserRefreshing = _isUserRefreshing.asStateFlow()

    private fun getUserInfo(handle: String) {
        viewModelScope.launch {
            codeforcesRepository.getUserInfo(handle = handle)
                .onStart {
                    _responseForUserInfo.update { ApiState.Loading }
                    _isUserRefreshing.update { true }
                }.catch { e ->
                    val errorMessage = ErrorEntity.getError(e).messageId
                    _responseForUserInfo.update {
                        ApiState.Failure(
                            UiText.StringResource(
                                errorMessage
                            )
                        )
                    }
                    _isUserRefreshing.update { false }
                    Log.e(TAG, "getUserInfo: $e")
                }.collect {apiResult ->
                    if (apiResult.status == "OK") {
                        codeforcesDatabase.setUser(user = apiResult.result!![0])
                        _responseForUserInfo.update { ApiState.Success }
                        Log.d(TAG, "Got - User Info - $handle")
                    } else {
                        _responseForUserInfo.update { ApiState.Failure(UiText.DynamicString(apiResult.comment.toString())) }
                        Log.e(TAG, "getUserInfo: " + apiResult.comment.toString())
                    }
                    _isUserRefreshing.update { false }
                }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userPreferences.setHandleName("")
        }
    }

    companion object {
        private const val TAG = "User ViewModel"
    }

    init {
        refreshUserInfo()
    }
}