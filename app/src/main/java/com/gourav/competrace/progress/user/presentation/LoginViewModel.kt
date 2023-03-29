package com.gourav.competrace.progress.user.presentation

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.R
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.data.repository.CodeforcesRepository
import com.gourav.competrace.app_core.util.SnackbarManager
import com.gourav.competrace.app_core.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val codeforcesRepository: CodeforcesRepository,
    private val userPreferences: UserPreferences
)  : ViewModel() {

    fun checkUsernameAvailable(handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            codeforcesRepository.getUserInfo(handle = handle)
                .onStart {

                }.catch {
                    Log.e(TAG, it.toString())
                }.collect {
                    if(it.status == "OK"){
                        userPreferences.setHandleName(handle)
                        Log.d(TAG, "Got - User Info - $handle")
                    } else {
                        Log.e(TAG, it.comment.toString())
                    }
                }
        }
    }

    private val _inputHandle = MutableStateFlow("")
    val inputHandle = _inputHandle.asStateFlow()

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