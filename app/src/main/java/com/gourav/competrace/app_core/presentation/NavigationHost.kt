package com.gourav.competrace.app_core

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.pager.ExperimentalPagerApi
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.ui.navigation.navsections.contests
import com.gourav.competrace.ui.navigation.navsections.problemSet
import com.gourav.competrace.ui.navigation.navsections.progress

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationHost(
    applicationViewModel: ApplicationViewModel,
    mainViewModel: MainViewModel,
    navController: NavHostController,
    paddingValues: PaddingValues,
) {

    val coroutineScope = rememberCoroutineScope()
    val userPreferences = UserPreferences(LocalContext.current.applicationContext)
    val topAppBarController = applicationViewModel.topAppBarController

    NavHost(
        navController = navController, startDestination = Screens.ContestsScreen.name,
        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
    ) {
        val homeScreens = listOf(
            Screens.ContestsScreen.title,
            Screens.ProblemSetScreen.title,
            Screens.ProgressScreen.title
        )
        topAppBarController.onClickNavUp =
            { if (!homeScreens.contains(topAppBarController.screenTitle)) navController.navigateUp() }


        contests(
            applicationViewModel = applicationViewModel,
            mainViewModel = mainViewModel,
            coroutineScope = coroutineScope,
            userPreferences = userPreferences,
        )

        problemSet(
            applicationViewModel = applicationViewModel,
            mainViewModel = mainViewModel,
            userPreferences = userPreferences
        )

        progress(
            applicationViewModel = applicationViewModel,
            mainViewModel = mainViewModel,
            userPreferences = userPreferences,
            navController = navController
        )

    }
}