package com.gourav.competrace.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.pager.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.model.Contest
import com.gourav.competrace.model.UserRatingChanges
import com.gourav.competrace.retrofit.util.ApiState
import com.gourav.competrace.ui.components.CircularProgressIndicator
import com.gourav.competrace.ui.components.ContestScreenActions
import com.gourav.competrace.ui.components.FailureTag
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.controllers.TopAppBarController
import com.gourav.competrace.ui.navigation.Screens
import com.gourav.competrace.ui.screens.FinishedContestScreen
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.ui.screens.UpcomingContestScreen
import com.gourav.competrace.utils.FinishedContestFilter
import com.gourav.competrace.utils.Phase
import com.gourav.competrace.utils.pagerTabIndicatorOffset
import com.gourav.competrace.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi // 1.
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    topAppBarController: TopAppBarController,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    userPreferences: UserPreferences,
) {

    composable(route = Screens.ContestsScreen.name) {
        topAppBarController.apply {
            title = Screens.ContestsScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

        val tabTitles =
            listOf(Screens.UpcomingContestsScreen.title, Screens.FinishedContestsScreen.title)
        val pagerState = rememberPagerState(initialPage = 0)
        val tabIndex = pagerState.currentPage

        var isSettingsDialogueOpen by remember {
            mutableStateOf(false)
        }

        val dismissSettingsDialog: () -> Unit = {
            isSettingsDialogueOpen = false
        }

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = dismissSettingsDialog,
            userPreferences = userPreferences
        )

        var currentSelection by rememberSaveable {
            mutableStateOf(FinishedContestFilter.GIVEN)
        }

        topAppBarController.actions = {
            ContestScreenActions(
                onClickSettings = { isSettingsDialogueOpen = true },
                currentSelectionForFinishedContests = currentSelection,
                onClickAll = { currentSelection = FinishedContestFilter.ALL },
                onClickGiven = { currentSelection = FinishedContestFilter.GIVEN }
            )
        }

        val isRefreshing = mainViewModel.isContestListRefreshing.collectAsState().value
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = mainViewModel::refreshContestList,
        ) {
            Column {
                TabRow(
                    selectedTabIndex = tabIndex,
                    indicator = { tabPositions -> // 3.
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(
                                pagerState,
                                tabPositions
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
                                    text = title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(isDisplayed = true)
                            }
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
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator(isDisplayed = true)
                                                    }
                                                }
                                                is ApiState.Success<*> -> {
                                                    val userRatingChanges =
                                                        apiResultForUserRatingChanges.response.result as List<UserRatingChanges>

                                                    userRatingChanges.forEach { userRatingChange ->
                                                        mainViewModel.contestListsByPhase[Phase.FINISHED]!!.find {
                                                            it.id == userRatingChange.contestId
                                                        }?.also {
                                                            it.isAttempted = true
                                                            it.ratingChange =
                                                                userRatingChange.newRating - userRatingChange.oldRating
                                                            it.rank = userRatingChange.rank
                                                            it.newRating =
                                                                userRatingChange.newRating
                                                        }
                                                    }
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
                                                else {
                                                    val resultList = ArrayList<Contest>()
                                                    finishedContests.forEach {
                                                        if (it.isAttempted) resultList.add(it)
                                                    }
                                                    resultList
                                                }

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
                        is ApiState.Empty -> {}
                        else -> { }
                    }
                }
            }
        }
    }
}