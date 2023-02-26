package com.gourav.competrace.app_core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.app_core.data.repository.CodeforcesRepository
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarController
import com.gourav.competrace.app_core.util.TopAppBarStyles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val codeforcesRepository: CodeforcesRepository
) : ViewModel() {

    private val _isSplashScreenOn = MutableStateFlow(true)
    val isSplashScreenOn = _isSplashScreenOn.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1_000)
            _isSplashScreenOn.update { false }
        }
    }

    val topAppBarController = object : TopAppBarController {
        override var screenTitle: String by mutableStateOf(Screens.ContestsScreen.title)

        override var topAppBarStyle: TopAppBarStyles by mutableStateOf(TopAppBarStyles.Small)

        override var onClickNavUp: () -> Unit by mutableStateOf({})

        override var isTopAppBarExpanded: Boolean by mutableStateOf(false)

        override var expandedTopAppBarContent: @Composable () -> Unit by mutableStateOf({})

        override var actions: @Composable RowScope.() -> Unit by mutableStateOf({})

        override fun clearActions() {
            actions = {}
        }

        override var isSearchWidgetOpen: Boolean by mutableStateOf(false)

        override var searchWidgetContent: @Composable () -> Unit by mutableStateOf({})
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

    fun setIsPlatformsTabRowVisible(value: Boolean){
        _isPlatformsTabRowVisible.update { value }
    }

    companion object {
        private const val TAG = "Shared ViewModel"
    }
}
