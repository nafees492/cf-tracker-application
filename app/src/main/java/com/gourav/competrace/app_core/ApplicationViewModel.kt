package com.gourav.competrace.app_core

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gourav.competrace.ui.controllers.TopAppBarController
import com.gourav.competrace.ui.controllers.TopAppBarStyles
import com.gourav.competrace.app_core.util.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ApplicationViewModel : ViewModel() {

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
}
