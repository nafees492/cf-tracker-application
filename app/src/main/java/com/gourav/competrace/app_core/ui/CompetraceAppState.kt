package com.gourav.competrace.app_core.ui

import android.content.res.Resources
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.SnackbarManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun rememberCompetraceAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
    CompetraceAppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope)
}


@Stable
class CompetraceAppState(
    val snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    // Process snackbars coming from SnackbarManager
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                if (currentMessages.isNotEmpty()) {
                    val message = currentMessages[0]
                    val text = resources.getString(message.messageId)
                    val actionLabel = message.actionLabelId?.let { resources.getString(it) }

                    val action = snackbarHostState.showSnackbar(
                        message = text,
                        actionLabel = actionLabel,
                        duration = SnackbarDuration.Short
                    )

                    if(action == SnackbarResult.ActionPerformed) message.action()

                    snackbarManager.setMessageShown(message.id)
                }
            }
        }
    }

    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    val bottomBarTabs = Screens.values().filter { it.isHomeScreen }
    private val bottomBarRoutes = bottomBarTabs.map { it.route }

    val shouldShowBottomBar: Boolean
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination?.route in bottomBarRoutes

    fun navigateToBottomBarRoute(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }
}

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)


private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}

@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
