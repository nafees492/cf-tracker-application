package com.gourav.competrace.progress.user.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.repository.CodeforcesRepository
import com.gourav.competrace.app_core.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel@Inject constructor (
    private val codeforcesRepository: CodeforcesRepository
)  : ViewModel() {

    var responseForCheckUsernameAvailable by mutableStateOf<ApiState>(ApiState.Empty)

    fun checkUsernameAvailable(handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            codeforcesRepository.getUserInfo(handle = handle)
                .onStart {
                    responseForCheckUsernameAvailable = ApiState.Loading
                }.catch {
                    responseForCheckUsernameAvailable = ApiState.Failure
                    Log.e(TAG, it.toString())
                }.collect {
                    if(it.status == "OK"){
                        responseForCheckUsernameAvailable = ApiState.Success
                        Log.d(TAG, "Got - User Info - $handle")
                    } else {
                        responseForCheckUsernameAvailable = ApiState.Failure
                        Log.e(TAG, it.comment.toString())
                    }
                }
        }
    }

    private val _inputHandle = MutableStateFlow("")
    val inputHandle = _inputHandle.asStateFlow()

    fun onInputHandleChange(value: String){
        _inputHandle.update { value }
    }

    companion object {
        private const val TAG = "Login ViewModel"
    }
}