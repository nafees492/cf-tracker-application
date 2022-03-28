package com.example.cfprogresstracker.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.navsections.contests
import com.example.cfprogresstracker.ui.navigation.navsections.problemSet
import com.example.cfprogresstracker.ui.navigation.navsections.progress
import com.example.cfprogresstracker.viewmodel.MainViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationHost(
    toolbarController: ToolbarController,
    navController: NavHostController,
    navigateToLoginActivity: () -> Unit,
    mainViewModel: MainViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val userPreferences = UserPreferences(LocalContext.current.applicationContext)

    var requestedForUserInfo by remember { mutableStateOf(false) }
    var requestedForUserSubmission by remember { mutableStateOf(false) }

    Log.d("", requestedForUserInfo.toString())
    Log.d("", requestedForUserSubmission.toString())


    NavHost(
        navController = navController, startDestination = Screens.ContestsScreen.name
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
            requestedForUserSubmission = requestedForUserSubmission,
            toggleRequestedForUserSubmissionTo = { requestedForUserSubmission = it }
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
            navigateToLoginActivity = navigateToLoginActivity,
            requestedForUserInfo = requestedForUserInfo,
            toggleRequestedForUserInfoTo = { requestedForUserInfo = it },
            requestedForUserSubmission = requestedForUserSubmission,
            toggleRequestedForUserSubmissionTo = { requestedForUserSubmission = it }
        )

    }
}