package com.theruralguys.competrace.ui.controllers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
interface TopAppBarController {
    var title: String
    var scrollBehavior: TopAppBarScrollBehavior
    var toolbarStyle: ToolbarStyles
    var onClickNavUp: () -> Unit
    var expandToolbar: Boolean
    var expandedContent: @Composable () -> Unit
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