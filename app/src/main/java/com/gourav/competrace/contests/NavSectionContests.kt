package com.gourav.competrace.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.pager.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.ApplicationViewModel
import com.gourav.competrace.app_core.MainViewModel
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.contests.ContestViewModel
import com.gourav.competrace.contests.model.Contest
import com.gourav.competrace.contests.util.pagerTabIndicatorOffset
import com.gourav.competrace.ui.components.CircularProgressIndicator
import com.gourav.competrace.ui.components.ContestScreenActions
import com.gourav.competrace.ui.components.FailureTag
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.screens.FinishedContestScreen
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.ui.screens.UpcomingContestScreen
import com.gourav.competrace.utils.FinishedContestFilter
import com.gourav.competrace.utils.Phase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi // 1.
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    applicationViewModel: ApplicationViewModel,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    userPreferences: UserPreferences,
) {
    val topAppBarController = applicationViewModel.topAppBarController

    composable(route = Screens.ContestsScreen.name) {
        val contestViewModel: ContestViewModel = viewModel()

        topAppBarController.apply {
            screenTitle = Screens.ContestsScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

        val isSettingsDialogueOpen by applicationViewModel.isSettingsDialogueOpen.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = applicationViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        val currentSelection by contestViewModel.currentSelection.collectAsState(initial = FinishedContestFilter.PARTICIPATED)

        topAppBarController.actions = {
            ContestScreenActions(onClickSettings = applicationViewModel::openSettingsDialog,
                currentSelectionForFinishedContests = currentSelection,
                onClickAll = { contestViewModel.updateCurrentSelection(FinishedContestFilter.ALL) },
                onClickGiven = { contestViewModel.updateCurrentSelection(FinishedContestFilter.PARTICIPATED) } )
        }

        val pagerState = rememberPagerState(initialPage = 0)
        val tabIndex = pagerState.currentPage

        val tabTitles =
            listOf(Screens.UpcomingContestsScreen.title, Screens.FinishedContestsScreen.title)

        val isRefreshing by mainViewModel.isContestListRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = mainViewModel::refreshContestList,
        ) {
            Column {
                TabRow(
                    selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(
                                pagerState, tabPositions
                            )
                        )
                    },
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title, maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                HorizontalPager(
                    count = tabTitles.size,
                    state = pagerState,
                ) { index ->
                    when (val apiResult = mainViewModel.responseForContestList) {
                        is ApiState.Loading -> {
                            CircularProgressIndicator(
                                isDisplayed = true, modifier = Modifier.fillMaxSize()
                            )
                        }
                        is ApiState.Success<*> -> {
                            if (apiResult.response.status == "OK") {
                                when (index) {
                                    0 -> {
                                        UpcomingContestScreen(
                                            contestLists = mainViewModel.contestListsByPhase,
                                            contestListBefore = mainViewModel.contestListBeforeByPhase,
                                        )
                                    }
                                    1 -> {
                                        Column {
                                            when (val apiResultForUserRatingChanges =
                                                mainViewModel.responseForUserRatingChanges) {
                                                is ApiState.Loading -> {
                                                    CircularProgressIndicator(
                                                        isDisplayed = true,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                                is ApiState.Failure -> {
                                                    FailureTag(onClickRetry = {
                                                        mainViewModel.requestForUserRatingChanges(
                                                            userPreferences = userPreferences,
                                                            isForced = true
                                                        )
                                                    })
                                                }
                                                is ApiState.Empty -> {
                                                    mainViewModel.requestForUserRatingChanges(
                                                        userPreferences = userPreferences,
                                                        isForced = false
                                                    )
                                                }
                                                else -> {}
                                            }
                                            val finishedContests: ArrayList<Contest> =
                                                (mainViewModel.contestListsByPhase[Phase.FINISHED] as ArrayList<Contest>?)!!

                                            val filteredContest =
                                                if (currentSelection == FinishedContestFilter.ALL) finishedContests
                                                else { finishedContests.filter { it.isAttempted } }

                                            FinishedContestScreen(finishedContests = filteredContest)
                                        }
                                    }
                                }

                            } else {
                                mainViewModel.responseForContestList = ApiState.Failure(Throwable())
                            }
                        }
                        is ApiState.Failure -> {
                            NetworkFailScreen(onClickRetry = { mainViewModel.getContestList() })
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}