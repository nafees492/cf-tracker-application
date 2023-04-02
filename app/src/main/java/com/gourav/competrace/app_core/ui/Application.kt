package com.gourav.competrace.app_core.ui


import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.gourav.competrace.app_core.ui.components.CompetraceBottomNavigationBar
import com.gourav.competrace.app_core.ui.components.CompetraceTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application(
    sharedViewModel: SharedViewModel
) {
    val appState = rememberCompetraceAppState()

    Surface {
        Scaffold(
            topBar = {
                CompetraceTopAppBar(sharedViewModel = sharedViewModel, appState = appState)
            },
            bottomBar = {
                CompetraceBottomNavigationBar(appState = appState)
            },
            snackbarHost = { SnackbarHost(hostState = appState.snackbarHostState) }
        ) {
            CompetraceNavHost(
                sharedViewModel = sharedViewModel,
                appState = appState,
                paddingValues = it
            )
        }
    }

}