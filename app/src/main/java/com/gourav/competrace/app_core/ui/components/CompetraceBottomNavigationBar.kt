package com.gourav.competrace.app_core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.CompetraceAppState

@Composable
fun CompetraceBottomNavigationBar(appState: CompetraceAppState) {
    AnimatedVisibility(
        visible = appState.shouldShowBottomBar,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        NavigationBar {
            appState.bottomBarTabs.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconId!!),
                            contentDescription = null
                        )
                    },
                    label = { Text(text = stringResource(id = item.titleId)) },
                    selected = appState.currentRoute == item.route,
                    onClick = {
                        appState.navigateToBottomBarRoute(item.route)
                    }
                )
            }
        }
    }
}