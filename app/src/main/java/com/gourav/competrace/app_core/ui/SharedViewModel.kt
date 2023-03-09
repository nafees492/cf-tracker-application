package com.gourav.competrace.app_core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _isSplashScreenOn = MutableStateFlow(true)
    val isSplashScreenOn = _isSplashScreenOn.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_000)
            _isSplashScreenOn.update { false }
        }
    }

    private val _isSettingsDialogueOpen = MutableStateFlow(false)
    val isSettingsDialogueOpen = _isSettingsDialogueOpen.asStateFlow()

    fun dismissSettingsDialog() {
        _isSettingsDialogueOpen.update { false }
    }

    fun openSettingsDialog() {
        _isSettingsDialogueOpen.update { true }
    }

    private val _isPlatformsTabRowVisible = MutableStateFlow(true)
    val isPlatformsTabRowVisible = _isPlatformsTabRowVisible.asStateFlow()

    fun setIsPlatformsTabRowVisible(value: Boolean) {
        _isPlatformsTabRowVisible.update { value }
    }

    companion object {
        private const val TAG = "Shared ViewModel"
    }
}
