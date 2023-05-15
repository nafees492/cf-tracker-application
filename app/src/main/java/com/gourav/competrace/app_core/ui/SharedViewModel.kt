package com.gourav.competrace.app_core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedViewModel: ViewModel() {

    private val _isSplashScreenOn = MutableStateFlow(true)
    val isSplashScreenOn = _isSplashScreenOn.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_000)
            _isSplashScreenOn.update { false }
        }
    }

    companion object {
        private const val TAG = "Shared ViewModel"
    }
}
