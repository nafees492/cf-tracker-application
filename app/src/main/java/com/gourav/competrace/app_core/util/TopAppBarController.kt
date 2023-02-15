package com.gourav.competrace.ui.controllers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

interface TopAppBarController {
    var screenTitle: String
    var topAppBarStyle: TopAppBarStyles
    var onClickNavUp: () -> Unit
    var isTopAppBarExpanded: Boolean
    var expandedTopAppBarContent: @Composable () -> Unit
    var actions: @Composable RowScope.() -> Unit
    fun clearActions()
    var isSearchWidgetOpen: Boolean
    var searchWidgetContent: @Composable () -> Unit
}

sealed interface TopAppBarStyles {
    object Small : TopAppBarStyles
    object Medium : TopAppBarStyles
    object Large : TopAppBarStyles
}