package com.gourav.competrace.app_core.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.gourav.competrace.app_core.ApplicationViewModel
import com.gourav.competrace.app_core.MainViewModel
import com.gourav.competrace.app_core.NavigationHost
import com.gourav.competrace.app_core.ui.components.CompetraceBottomNavigationBar
import com.gourav.competrace.ui.components.CompetraceTopAppBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Application(
    mainViewModel: MainViewModel,
    applicationViewModel: ApplicationViewModel
) {
    val navController = rememberNavController()

    Surface {
        Scaffold(
            topBar = {
                CompetraceTopAppBar(
                    topAppBarController = applicationViewModel.topAppBarController
                )
            },
            bottomBar = {
                CompetraceBottomNavigationBar(
                    navController = navController,
                    currentScreen = applicationViewModel.topAppBarController.screenTitle
                )
            }
        ) {
            NavigationHost(
                applicationViewModel = applicationViewModel,
                mainViewModel = mainViewModel,
                navController = navController,
                paddingValues = it,
            )
        }
    }

}