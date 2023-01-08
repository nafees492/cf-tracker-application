package com.theruralguys.competrace.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.pager.ExperimentalPagerApi
import com.theruralguys.competrace.data.UserPreferences
import com.theruralguys.competrace.ui.controllers.TopAppBarController
import com.theruralguys.competrace.ui.navigation.navsections.contests
import com.theruralguys.competrace.ui.navigation.navsections.problemSet
import com.theruralguys.competrace.ui.navigation.navsections.progress
import com.theruralguys.competrace.viewmodel.MainViewModel

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationHost(
    topAppBarController: TopAppBarController,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    paddingValues: PaddingValues,
    navigateToLoginActivity: () -> Unit,
    navigateToSettingsActivity: () -> Unit,

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
        topAppBarController.onClickNavUp =
            { if (!homeScreens.contains(topAppBarController.title)) navController.navigateUp() }


        contests(
            topAppBarController = topAppBarController,
            mainViewModel = mainViewModel,
            coroutineScope = coroutineScope,
            userPreferences = userPreferences,
            navController = navController,
            requestedForUserRatingChanges = requestedForUserRatingChanges,
            toggleRequestedForUserRatingChangesTo = { requestedForUserRatingChanges = it },
        )

        problemSet(
            topAppBarController = topAppBarController,
            mainViewModel = mainViewModel
        )

        progress(
            topAppBarController = topAppBarController,
            coroutineScope = coroutineScope,
            mainViewModel = mainViewModel,
            userPreferences = userPreferences,
            navController = navController,
            requestedForUserInfo = requestedForUserInfo,
            toggleRequestedForUserInfoTo = { requestedForUserInfo = it },
            requestedForUserSubmission = requestedForUserSubmission,
            toggleRequestedForUserSubmissionTo = { requestedForUserSubmission = it },
            navigateToLoginActivity = navigateToLoginActivity,
            navigateToSettingsActivity = navigateToSettingsActivity
        )

    }
}