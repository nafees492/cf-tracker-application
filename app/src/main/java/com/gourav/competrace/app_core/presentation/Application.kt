package com.gourav.competrace.app_core.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.gourav.competrace.app_core.ui.components.CompetraceBottomNavigationBar
import com.gourav.competrace.app_core.ui.components.CompetraceTopAppBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Application(
    sharedViewModel: SharedViewModel
) {
    val navController = rememberNavController()

    Surface {
        Scaffold(
            topBar = {
                CompetraceTopAppBar(sharedViewModel = sharedViewModel)
            },
            bottomBar = {
                CompetraceBottomNavigationBar(
                    navController = navController,
                    currentScreen = sharedViewModel.topAppBarController.screenTitle
                )
            }
        ) {
            NavigationHost(
                sharedViewModel = sharedViewModel,
                navController = navController,
                paddingValues = it,
            )
        }
    }

}