package com.gourav.competrace.app_core.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import com.gourav.competrace.app_core.model.TopAppBarValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object TopAppBarManager {
    private val topAppBarValuesFlow = MutableStateFlow(TopAppBarValues())
    val topAppBarValues = topAppBarValuesFlow.asStateFlow()

    fun updateTopAppBar(
        screen: Screens,
        isTopAppBarExpanded: Boolean = false,
        expandedTopAppBarContent: @Composable () -> Unit = {},
        isSearchWidgetOpen: Boolean = false,
        searchWidget: @Composable () -> Unit = {},
        actions: @Composable RowScope.() -> Unit = {}
    ){
        topAppBarValuesFlow.update {
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
        topAppBarValuesFlow.update {
            it.copy(isSearchWidgetOpen = true)
        }
    }

    fun closeSearchWidget(){
        topAppBarValuesFlow.update {
            it.copy(isSearchWidgetOpen = false)
        }
    }

    fun toggleExpandedState(){
        topAppBarValuesFlow.update {
            val isExpanded = it.isTopAppBarExpanded
            it.copy(isTopAppBarExpanded = !isExpanded)
        }
    }
}