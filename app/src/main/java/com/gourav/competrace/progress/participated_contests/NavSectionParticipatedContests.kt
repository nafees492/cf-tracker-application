package com.gourav.competrace.progress.participated_contests

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarManager
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestViewModel
import com.gourav.competrace.progress.participated_contests.presentation.ParticipatedContestsScreen
import com.gourav.competrace.app_core.ui.NetworkFailScreen

fun NavGraphBuilder.participatedContests(
    participatedContestViewModel: ParticipatedContestViewModel,
) {
    composable(route = Screens.ParticipatedContestsScreen.route) {

        val isRefreshing by participatedContestViewModel.isUserRatingChangesRefreshing.collectAsState()
        val responseForUserRatingChanges by participatedContestViewModel.responseForUserRatingChanges.collectAsState()
        val participatedContests by participatedContestViewModel.participatedContests.collectAsState()

        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        LaunchedEffect(Unit){
            TopAppBarManager.updateTopAppBar(screen = Screens.ParticipatedContestsScreen)
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = participatedContestViewModel::refreshUserRatingChanges,
            indicator = CompetraceSwipeRefreshIndicator
        ) {
            when (responseForUserRatingChanges) {
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(onClickRetry = participatedContestViewModel::refreshUserRatingChanges)
                }
                ApiState.Success -> {
                    ParticipatedContestsScreen(participatedCodeforcesContests = participatedContests)
                }
            }
        }
    }
}