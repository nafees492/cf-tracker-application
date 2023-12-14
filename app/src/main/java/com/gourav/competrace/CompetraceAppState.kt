package com.gourav.competrace

import android.content.Context
import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gourav.competrace.app_core.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
fun rememberCompetraceAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current
) = remember(
    snackbarHostState,
    navController,
    snackbarManager,
    resources,
    coroutineScope,
    context
) {
    CompetraceAppState(
        snackbarHostState,
        navController,
        snackbarManager,
        resources,
        coroutineScope,
        context
    )
}



@Stable
class CompetraceAppState(
    val snackbarHostState: SnackbarHostState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope,
    context: Context
) {
    init {
        coroutineScope.launch {
            snackbarManager.messages.collect { currentMessages ->
                currentMessages?.let { message ->
                    snackbarHostState.currentSnackbarData?.dismiss()

                    val text = message.message.asString(context)
                    val actionLabel = message.actionLabel?.asString(context)
                    val duration = message.duration

                    launch {
                        val action = snackbarHostState.showSnackbar(
                            message = text,
                            actionLabel = actionLabel,
                            duration = duration
                        )

                        if (action == SnackbarResult.ActionPerformed) message.action()

                        snackbarManager.setMessageShown(message.id)
                    }
                }
            }
        }
    }

    private val networkConnectivityObserver = ConnectivityObserverImpl(context)

    val isConnectedToNetwork = networkConnectivityObserver.observe().map {
        when(it){
            ConnectivityObserver.Status.Available -> true
            else -> false
        }
    }.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    private val _isPlatformsTabRowVisible = MutableStateFlow(false)
    val isPlatformsTabRowVisible = _isPlatformsTabRowVisible.asStateFlow()

    fun toggleIsPlatformsTabRowVisibleTo(value: Boolean) {
        _isPlatformsTabRowVisible.update { value }
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

    fun navigateToSettings(){
        navController.navigate(route = Screens.SettingsScreen.route)
    }

    fun navigateToUserSubmissionScreen(){
        navController.navigate(route = Screens.UserSubmissionsScreen.route)
    }

    fun navigateToParticipatedContestsScreen(){
        navController.navigate(route = Screens.ParticipatedContestsScreen.route)
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
