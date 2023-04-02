package com.gourav.competrace.progress.user.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.R
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.remote.CodeforcesRepository
import com.gourav.competrace.app_core.util.SnackbarManager
import com.gourav.competrace.app_core.util.UiText
import com.gourav.competrace.progress.user.data.repository.UserRepository
import com.gourav.competrace.progress.user.model.CompetraceUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences,
    private val userRepository: UserRepository
)  : ViewModel() {

    private val _inputHandle = MutableStateFlow("")
    val inputHandle = _inputHandle.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val userList = userRepository.getAllUsers().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun setUser(handle: String){
        viewModelScope.launch {
            userPreferences.setHandleName(handle)
        }
    }

    private fun addUserToDatabase(handle: String){
        viewModelScope.launch {
            userRepository.addUser(CompetraceUser(handle))
        }
    }

    fun deleteUserFromDatabase(handle: String){
        viewModelScope.launch {
            userRepository.deleteUser(CompetraceUser(handle))
        }
    }

    fun deleteAllUsersFromDatabase(){
        viewModelScope.launch{
            userRepository.deleteAllUsers()
        }
    }

    fun checkUsernameAvailable(handle: String) {
        viewModelScope.launch {
            codeforcesRepository.getUserInfo(handle = handle)
                .onStart {
                    _isLoading.update { true }
                }.catch {e ->
                    Log.e(TAG, e.toString())
                    SnackbarManager.showMessage(UiText.StringResource(R.string.user_not_found))
                    _isLoading.update { false }
                }.collect { apiResult ->
                    if(apiResult.status == "OK"){

                        val userHandle = apiResult.result!![0].handle

                        userPreferences.setHandleName(userHandle)
                        addUserToDatabase(userHandle)
                        Log.d(TAG, "Got - User Info - $userHandle")
                    } else {
                        Log.e(TAG, apiResult.comment.toString())
                        SnackbarManager.showMessage(UiText.StringResource(R.string.user_not_found))
                    }
                    _isLoading.update { false }
                }
        }
    }

    val isValidHandle = inputHandle.map {
        !(it.length < 3 || it.contains(' ') || it.contains(';'))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    fun onInputHandleChange(value: String){
        _inputHandle.update { value }
    }

    companion object {
        private const val TAG = "Login ViewModel"
    }
}