package com.gourav.competrace.contests

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
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarManager
import com.gourav.competrace.contests.presentation.ContestScreenActions
import com.gourav.competrace.contests.presentation.ContestSites
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.contests.presentation.UpcomingContestScreen
import com.gourav.competrace.settings.SettingsAlertDialog
import com.gourav.competrace.app_core.ui.NetworkFailScreen
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.contests(
    sharedViewModel: SharedViewModel,
    contestViewModel: ContestViewModel
) {
    composable(route = Screens.ContestsScreen.route) {

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()
        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        val selectedIndex by contestViewModel.selectedIndex.collectAsState()
        val currentContests by contestViewModel.contests.collectAsState()
        val notificationContestIdList by contestViewModel.notificationContestIdList.collectAsState()

        val responseForKontestsContestList by contestViewModel.responseForKontestsContestList.collectAsState()
        val isRefreshing by contestViewModel.isKontestsContestListRefreshing.collectAsState()

        LaunchedEffect(Unit){
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ContestsScreen,
                actions = {
                    ContestScreenActions(
                        onClickSettings = sharedViewModel::openSettingsDialog,
                        clearAllNotifications = contestViewModel::clearAllNotifications
                    )
                }
            )
        }

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = sharedViewModel::dismissSettingsDialog
        )

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
                onRefresh = contestViewModel::getContestListFromKontests,
                indicator = CompetraceSwipeRefreshIndicator
            ) {
                when (responseForKontestsContestList) {
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(
                            onClickRetry = contestViewModel::getContestListFromKontests
                        )
                    }
                    is ApiState.Success -> {
                       UpcomingContestScreen(
                           contests = currentContests,
                           selectedIndex = selectedIndex,
                           onClickNotificationIcon = contestViewModel::toggleContestNotification,
                           notificationContestIdList = notificationContestIdList
                       )
                    }
                }
            }
        }
    }
}
