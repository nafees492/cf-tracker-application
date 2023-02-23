package com.gourav.competrace.app_core.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.pager.ExperimentalPagerApi
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.contests.contests
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.problemset.problemSet
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestViewModel
import com.gourav.competrace.progress.progress
import com.gourav.competrace.progress.user.presentation.UserViewModel
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NavigationHost(
    sharedViewModel: SharedViewModel,
    navController: NavHostController,
    paddingValues: PaddingValues,
    userPreferences: UserPreferences = UserPreferences(LocalContext.current)
) {
    val topAppBarController = sharedViewModel.topAppBarController
    val scope = rememberCoroutineScope()

    val contestViewModel: ContestViewModel = hiltViewModel()
    val problemSetViewModel: ProblemSetViewModel = hiltViewModel()
    val userSubmissionsViewModel: UserSubmissionsViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val participatedContestViewModel: ParticipatedContestViewModel = hiltViewModel()

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
            sharedViewModel = sharedViewModel,
            contestViewModel = contestViewModel,
            userPreferences = userPreferences,
        )

        problemSet(
            sharedViewModel = sharedViewModel,
            problemSetViewModel = problemSetViewModel,
            userPreferences = userPreferences
        )

        progress(
            sharedViewModel = sharedViewModel,
            userViewModel = userViewModel,
            userSubmissionsViewModel = userSubmissionsViewModel,
            participatedContestViewModel = participatedContestViewModel,
            userPreferences = userPreferences,
            navController = navController
        )
    }
}