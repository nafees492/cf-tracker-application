package com.gourav.competrace.progress

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import com.gourav.competrace.CompetraceAppState
import com.gourav.competrace.progress.participated_contests.participatedContests
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestViewModel
import com.gourav.competrace.progress.user.presentation.UserViewModel
import com.gourav.competrace.progress.user.user
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.progress.user_submissions.userSubmission

fun NavGraphBuilder.progress(
    userViewModel: UserViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    participatedContestViewModel: ParticipatedContestViewModel,
    appState: CompetraceAppState,
    paddingValues: PaddingValues
) {

    user(
        userViewModel = userViewModel,
        userSubmissionsViewModel = userSubmissionsViewModel,
        appState = appState,
        paddingValues = paddingValues
    )

    userSubmission(
        userSubmissionsViewModel = userSubmissionsViewModel,
    )

    participatedContests(
        participatedContestViewModel = participatedContestViewModel,
    )
}