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
import com.google.accompanist.pager.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.presentation.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.MyCircularProgressIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.contests.presentation.ContestScreenActions
import com.gourav.competrace.contests.presentation.ContestSites
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.contests.presentation.UpcomingContestScreen
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.screens.NetworkFailScreen
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@ExperimentalPagerApi // 1.
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    sharedViewModel: SharedViewModel,
    contestViewModel: ContestViewModel,
    userPreferences: UserPreferences,
) {
    val topAppBarController = sharedViewModel.topAppBarController

    composable(route = Screens.ContestsScreen.name) {

        val scope = rememberCoroutineScope()

        topAppBarController.apply {
            screenTitle = Screens.ContestsScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = sharedViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        topAppBarController.actions = {
            ContestScreenActions(onClickSettings = sharedViewModel::openSettingsDialog)
        }

        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()
        val tabTitles = ContestSites.values().map { it.title }

        val codeforcesOnGoingContests by contestViewModel.codeforcesOnGoingContests.collectAsState()
        val codeforcesUpComingContests by contestViewModel.codeforcesUpComingContests.collectAsState()

        val codeChefOnGoingContests by contestViewModel.codeChefOnGoingContests.collectAsState()
        val codeChefUpComingContests by contestViewModel.codeChefUpComingContests.collectAsState()

        val atCoderOnGoingContests by contestViewModel.atCoderOnGoingContests.collectAsState()
        val atCoderUpComingContests by contestViewModel.atCoderUpComingContests.collectAsState()

        val leetCodeOnGoingContests by contestViewModel.leetCodeOnGoingContests.collectAsState()
        val leetCodeUpComingContests by contestViewModel.leetCodeUpComingContests.collectAsState()

        val kickStartOnGoingContests by contestViewModel.kickStartOnGoingContests.collectAsState()
        val kickStartUpComingContests by contestViewModel.kickStartUpComingContests.collectAsState()

        val isRefreshing by contestViewModel.isKontestsContestListRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        val selectedIndex by userPreferences.selectedContestSiteIndexFlow.collectAsState(initial = 0)

        Column {
            AnimatedVisibility(
                visible = isPlatformTabRowVisible,
                modifier = Modifier.fillMaxWidth()
            ) {
                CompetracePlatformRow(
                    selectedTabIndex = selectedIndex,
                    tabTitles = tabTitles,
                    onClickTab = {
                        scope.launch {
                            userPreferences.setSelectedContestSiteIndex(it)
                        }
                    },
                )
            }

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = contestViewModel::refreshContestListFromKontests,
            ) {
                when (contestViewModel.responseForKontestsContestList) {
                    is ApiState.Empty -> {}
                    is ApiState.Loading -> {
                        MyCircularProgressIndicator(
                            isDisplayed = true, modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(
                            onClickRetry = {
                                contestViewModel.refreshContestListFromKontests()
                            }
                        )
                    }
                    is ApiState.Success -> {
                        when (selectedIndex) {
                            0 -> {
                                UpcomingContestScreen(
                                    onGoingContest = codeforcesOnGoingContests,
                                    upComingContests = codeforcesUpComingContests,
                                )
                            }
                            1 -> {
                                UpcomingContestScreen(
                                    onGoingContest = codeChefOnGoingContests,
                                    upComingContests = codeChefUpComingContests,
                                )
                            }
                            2 -> {
                                UpcomingContestScreen(
                                    onGoingContest = atCoderOnGoingContests,
                                    upComingContests = atCoderUpComingContests,
                                )
                            }
                            3 -> {
                                UpcomingContestScreen(
                                    onGoingContest = leetCodeOnGoingContests,
                                    upComingContests = leetCodeUpComingContests,
                                )
                            }
                            4 -> {
                                UpcomingContestScreen(
                                    onGoingContest = kickStartOnGoingContests,
                                    upComingContests = kickStartUpComingContests,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
