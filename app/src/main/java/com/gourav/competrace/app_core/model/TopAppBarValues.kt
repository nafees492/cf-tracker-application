package com.gourav.competrace.app_core.model

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.gourav.competrace.app_core.util.Screens

data class TopAppBarValues(
    val currentScreen: Screens = Screens.ContestsScreen,
    val actions: @Composable RowScope.() -> Unit = {},
    val isTopAppBarExpanded: Boolean = false,
    val expandedTopAppBarContent: @Composable () -> Unit = {},
    val isSearchWidgetOpen: Boolean = false,
    val searchWidget: @Composable () -> Unit = {}
)
