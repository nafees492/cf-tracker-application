package com.example.cfprogresstracker.ui.controllers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

interface ToolbarController {
    var title: String
    var scrollBehavior: TopAppBarScrollBehavior
    var toolbarStyle: ToolbarStyles
    var onClickNavUp: () -> Unit
    var searchWidgetState: SearchWidgetState
    var actions: @Composable RowScope.() -> Unit
    fun clearActions()
}

sealed class ToolbarStyles {
    object Small : ToolbarStyles()
    object Medium : ToolbarStyles()
    object Large : ToolbarStyles()
}


sealed class SearchWidgetState {
    class Opened(
        val onSearchClicked: (String) -> Unit,
        val onCloseClicked: () -> Unit
    ) : SearchWidgetState()

    class Closed : SearchWidgetState()
}