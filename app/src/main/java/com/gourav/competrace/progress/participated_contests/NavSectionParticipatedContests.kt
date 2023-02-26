package com.gourav.competrace.progress.participated_contests

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestViewModel
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestsScreen
import com.gourav.competrace.ui.screens.NetworkFailScreen

fun NavGraphBuilder.participatedContests(
    sharedViewModel: SharedViewModel,
    participatedContestViewModel: ParticipatedContestViewModel,
    userPreferences: UserPreferences,
) {
    val topAppBarController = sharedViewModel.topAppBarController

    composable(route = Screens.ParticipatedContestsScreen.name) {

        topAppBarController.apply {
            screenTitle = Screens.ParticipatedContestsScreen.title
            isTopAppBarExpanded = false
            clearActions()
        }


        val isRefreshing by participatedContestViewModel.isUserRatingChangesRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                participatedContestViewModel.requestForUserRatingChanges(
                    userPreferences = userPreferences,
                    isForced = true
                )
            },
            indicator = CompetraceSwipeRefreshIndicator
        ) {
            when (participatedContestViewModel.responseForUserRatingChanges) {
                is ApiState.Empty -> {
                    participatedContestViewModel.requestForUserRatingChanges(
                        userPreferences = userPreferences,
                        isForced = false
                    )
                }
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(onClickRetry = {
                        participatedContestViewModel.requestForUserRatingChanges(
                            userPreferences = userPreferences,
                            isForced = true
                        )
                    })
                }
                ApiState.Success -> {
                    val participatedContests by participatedContestViewModel.participatedContests.collectAsState()

                    ParticipatedContestsScreen(participatedCodeforcesContests = participatedContests)
                }
            }
        }
    }
}