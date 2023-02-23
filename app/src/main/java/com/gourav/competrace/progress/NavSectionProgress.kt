package com.gourav.competrace.progress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.presentation.SharedViewModel
import com.gourav.competrace.progress.participated_contests.participatedContests
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestViewModel
import com.gourav.competrace.progress.user.presentation.UserViewModel
import com.gourav.competrace.progress.user.user
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.progress.user_submissions.userSubmission

fun NavGraphBuilder.progress(
    sharedViewModel: SharedViewModel,
    userViewModel: UserViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    participatedContestViewModel: ParticipatedContestViewModel,
    userPreferences: UserPreferences,
    navController: NavController
) {

    user(
        sharedViewModel = sharedViewModel,
        userViewModel = userViewModel,
        userSubmissionsViewModel = userSubmissionsViewModel,
        userPreferences = userPreferences,
        navController = navController
    )

    userSubmission(
        sharedViewModel = sharedViewModel,
        userSubmissionsViewModel = userSubmissionsViewModel,
        userPreferences = userPreferences
    )

    participatedContests(
        sharedViewModel = sharedViewModel,
        participatedContestViewModel = participatedContestViewModel,
        userPreferences = userPreferences
    )
}