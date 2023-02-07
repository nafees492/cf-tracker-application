package com.gourav.competrace.ui.controllers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
interface TopAppBarController {
    var title: String
    var scrollBehavior: TopAppBarScrollBehavior
    var topAppBarStyle: TopAppBarStyles
    var onClickNavUp: () -> Unit
    var isTopAppBarExpanded: Boolean
    var expandedTopAppBarContent: @Composable () -> Unit
    var actions: @Composable RowScope.() -> Unit
    fun clearActions()
    var isSearchWidgetOpen: Boolean
    var searchWidgetContent: @Composable () -> Unit
    var fab: @Composable () -> Unit
}

sealed class TopAppBarStyles {
    object Small : TopAppBarStyles()
    object Medium : TopAppBarStyles()
    object Large : TopAppBarStyles()
}

sealed class SearchWidgetState {
    class Opened(val content: @Composable () -> Unit) : SearchWidgetState()
    object Closed : SearchWidgetState()
}