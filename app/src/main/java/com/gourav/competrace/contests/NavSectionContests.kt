package com.gourav.competrace.contests

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.contests.presentation.ContestScreenActions
import com.gourav.competrace.contests.presentation.ContestSites
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.contests.presentation.UpcomingContestScreen
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.screens.NetworkFailScreen
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    sharedViewModel: SharedViewModel,
    contestViewModel: ContestViewModel,
    userPreferences: UserPreferences,
) {
    val topAppBarController = sharedViewModel.topAppBarController

    composable(route = Screens.ContestsScreen.name) {

        topAppBarController.apply {
            screenTitle = Screens.ContestsScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()
        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        val selectedIndex by contestViewModel.selectedIndex.collectAsState()
        val currentContests by contestViewModel.currentContests.collectAsState()

        val responseForKontestsContestList by contestViewModel.responseForKontestsContestList.collectAsState()
        val isRefreshing by contestViewModel.isKontestsContestListRefreshing.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = sharedViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        topAppBarController.actions = {
            ContestScreenActions(onClickSettings = sharedViewModel::openSettingsDialog)
        }

        val tabTitles = ContestSites.values().map { it.title }

        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        Column {
            AnimatedVisibility(
                visible = isPlatformTabRowVisible,
                modifier = Modifier.fillMaxWidth()
            ) {
                CompetracePlatformRow(
                    selectedTabIndex = selectedIndex,
                    tabTitles = tabTitles,
                    onClickTab = contestViewModel::setSelectedIndexTo
                )
            }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = contestViewModel::refreshContestListFromKontests,
                indicator = CompetraceSwipeRefreshIndicator
            ) {
                when (responseForKontestsContestList) {
                    is ApiState.Empty -> {}
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(
                            onClickRetry = contestViewModel::refreshContestListFromKontests
                        )
                    }
                    is ApiState.Success -> {
                       UpcomingContestScreen(
                           contests = currentContests,
                           selectedIndex = selectedIndex
                       )
                    }
                }
            }
        }
    }
}
