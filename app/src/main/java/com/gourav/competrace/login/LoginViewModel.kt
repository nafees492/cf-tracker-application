package com.gourav.competrace.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.repository.MainRepository
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel@Inject constructor (
    private val mainRepository: MainRepository
)  : ViewModel() {

    var responseForCheckUsernameAvailable by mutableStateOf<ApiState>(ApiState.Empty)

    fun checkUsernameAvailable(handle: String) {
        viewModelScope.launch {
            mainRepository.getUserInfo(handle = handle)
                .onStart {
                    responseForCheckUsernameAvailable = ApiState.Loading
                }.catch {
                    responseForCheckUsernameAvailable = ApiState.Failure(it)
                    Log.e(MainViewModel.TAG, it.toString())
                }.collect {
                    responseForCheckUsernameAvailable = ApiState.Success(it)
                    Log.d(MainViewModel.TAG, "Got - User Info - $handle")
                }
        }
    }

    private val _inputHandle = MutableStateFlow("")
    val inputHandle = _inputHandle.asStateFlow()

    fun onInputHandleChange(value: String){
        _inputHandle.update { value }
    }

}