package com.gourav.competrace.app_core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceBottomNavigationBar
import com.gourav.competrace.app_core.ui.components.CompetraceTopAppBar
import com.gourav.competrace.app_core.util.ConnectionState
import com.gourav.competrace.app_core.util.SnackbarManager
import com.gourav.competrace.app_core.util.connectivityState
import com.gourav.competrace.app_core.util.observeConnectivityAsFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun Application(
    sharedViewModel: SharedViewModel
) {
    val appState = rememberCompetraceAppState()

    val connection by connectivityState()

    val isConnected = connection === ConnectionState.Available

    LaunchedEffect(key1 = isConnected) {
        if (isConnected) {
            appState.snackbarHostState.currentSnackbarData?.dismiss()
        } else {
            SnackbarManager.showMessage(
                messageTextId = R.string.network_unavailable,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

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