package com.gourav.competrace.app_core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val networkConnectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _isSplashScreenOn = MutableStateFlow(true)
    val isSplashScreenOn = _isSplashScreenOn.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_000)
            _isSplashScreenOn.update { false }
        }
    }

    val isConnectedToNetwork = networkConnectivityObserver.observe().map {
        when(it){
            ConnectivityObserver.Status.Available -> true
            else -> false
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    private val _isPlatformsTabRowVisible = MutableStateFlow(true)
    val isPlatformsTabRowVisible = _isPlatformsTabRowVisible.asStateFlow()

    fun toggleIsPlatformsTabRowVisibleTo(value: Boolean) {
        _isPlatformsTabRowVisible.update { value }
    }

    companion object {
        private const val TAG = "Shared ViewModel"
    }
}
