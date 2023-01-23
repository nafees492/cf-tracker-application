package com.example.competrace.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.competrace.data.UserPreferences
import com.example.competrace.ui.controllers.TopAppBarController
import com.example.competrace.ui.navigation.navsections.contests
import com.example.competrace.ui.navigation.navsections.problemSet
import com.example.competrace.ui.navigation.navsections.progress
import com.example.competrace.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationHost(
    topAppBarController: TopAppBarController,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues,
    navigateToLoginActivity: () -> Unit,
    ) {

    val coroutineScope = rememberCoroutineScope()
    val userPreferences = UserPreferences(LocalContext.current.applicationContext)

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
            { if (!homeScreens.contains(topAppBarController.title)) navController.navigateUp() }


        contests(
            topAppBarController = topAppBarController,
            mainViewModel = mainViewModel,
            coroutineScope = coroutineScope,
            userPreferences = userPreferences,
        )

        problemSet(
            topAppBarController = topAppBarController,
            mainViewModel = mainViewModel,
            userPreferences = userPreferences
        )

        progress(
            topAppBarController = topAppBarController,
            coroutineScope = coroutineScope,
            mainViewModel = mainViewModel,
            userPreferences = userPreferences,
            navController = navController,
            navigateToLoginActivity = navigateToLoginActivity,
        )

    }
}