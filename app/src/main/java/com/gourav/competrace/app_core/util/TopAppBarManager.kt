package com.gourav.competrace.app_core.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TopAppBarValues(
    val currentScreen: Screens = Screens.ContestsScreen,
    val actions: @Composable RowScope.() -> Unit = {},
    val isTopAppBarExpanded: Boolean = false,
    val expandedTopAppBarContent: @Composable () -> Unit = {},
    val isSearchWidgetOpen: Boolean = false,
    val searchWidget: @Composable () -> Unit = {}
)

object TopAppBarManager {
    private val _topAppBarValues = MutableStateFlow(TopAppBarValues())
    val topAppBarValues = _topAppBarValues.asStateFlow()

    fun updateTopAppBar(
        screen: Screens,
        isTopAppBarExpanded: Boolean = false,
        expandedTopAppBarContent: @Composable () -> Unit = {},
        isSearchWidgetOpen: Boolean = false,
        searchWidget: @Composable () -> Unit = {},
        actions: @Composable RowScope.() -> Unit = {}
    ){
        _topAppBarValues.update {
            it.copy(
                currentScreen = screen,
                actions = actions,
                isTopAppBarExpanded = isTopAppBarExpanded,
                expandedTopAppBarContent = expandedTopAppBarContent,
                isSearchWidgetOpen = isSearchWidgetOpen,
                searchWidget = searchWidget
            )
        }
    }

    fun openSearchWidget(){
        _topAppBarValues.update {
            it.copy(isSearchWidgetOpen = true)
        }
    }

    fun closeSearchWidget(){
        _topAppBarValues.update {
            it.copy(isSearchWidgetOpen = false)
        }
    }

    fun toggleExpandedState(){
        _topAppBarValues.update {
            val isExpanded = it.isTopAppBarExpanded
            it.copy(isTopAppBarExpanded = !isExpanded)
        }
    }
}