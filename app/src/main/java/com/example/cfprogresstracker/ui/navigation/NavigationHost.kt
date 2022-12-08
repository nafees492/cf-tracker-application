package com.example.cfprogresstracker.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.navsections.contests
import com.example.cfprogresstracker.ui.navigation.navsections.problemSet
import com.example.cfprogresstracker.ui.navigation.navsections.progress
import com.example.cfprogresstracker.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationHost(
    toolbarController: ToolbarController,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues,
    navigateToLoginActivity: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userPreferences = UserPreferences(LocalContext.current.applicationContext)

    var requestedForUserInfo by rememberSaveable { mutableStateOf(false) }
    var requestedForUserSubmission by rememberSaveable { mutableStateOf(false) }
    var requestedForUserRatingChanges by rememberSaveable { mutableStateOf(false) }

    NavHost(
        navController = navController, startDestination = Screens.ContestsScreen.name,
        modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
    ) {
        val homeScreens = listOf(
            Screens.ContestsScreen.title,
            Screens.ProblemSetScreen.title,
            Screens.ProgressScreen.title
        )
        toolbarController.onClickNavUp =
            { if (!homeScreens.contains(toolbarController.title)) navController.navigateUp() }


        contests(
            toolbarController = toolbarController,
            mainViewModel = mainViewModel,
            coroutineScope = coroutineScope,
            userPreferences = userPreferences,
            navController = navController,
            requestedForUserRatingChanges = requestedForUserRatingChanges,
            toggleRequestedForUserRatingChangesTo = { requestedForUserRatingChanges = it },
        )

        problemSet(
            toolbarController = toolbarController,
            mainViewModel = mainViewModel
        )

        progress(
            toolbarController = toolbarController,
            coroutineScope = coroutineScope,
            mainViewModel = mainViewModel,
            userPreferences = userPreferences,
            navController = navController,
            requestedForUserInfo = requestedForUserInfo,
            toggleRequestedForUserInfoTo = { requestedForUserInfo = it },
            requestedForUserSubmission = requestedForUserSubmission,
            toggleRequestedForUserSubmissionTo = { requestedForUserSubmission = it },
            navigateToLoginActivity = navigateToLoginActivity,
        )

    }
}