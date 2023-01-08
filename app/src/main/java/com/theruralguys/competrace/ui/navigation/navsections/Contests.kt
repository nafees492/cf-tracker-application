package com.theruralguys.competrace.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.pager.*
import com.theruralguys.competrace.data.UserPreferences
import com.theruralguys.competrace.model.Contest
import com.theruralguys.competrace.model.UserRatingChanges
import com.theruralguys.competrace.retrofit.util.ApiState
import com.theruralguys.competrace.ui.components.CircularIndeterminateProgressBar
import com.theruralguys.competrace.ui.components.ContestScreenActions
import com.theruralguys.competrace.ui.components.NormalButton
import com.theruralguys.competrace.ui.controllers.TopAppBarController
import com.theruralguys.competrace.ui.navigation.Screens
import com.theruralguys.competrace.ui.screens.FinishedContestScreen
import com.theruralguys.competrace.ui.screens.NetworkFailScreen
import com.theruralguys.competrace.ui.screens.UpcomingContestScreen
import com.theruralguys.competrace.utils.FinishedContestFilter
import com.theruralguys.competrace.utils.Phase
import com.theruralguys.competrace.utils.getTodaysDate
import com.theruralguys.competrace.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi // 1.
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    topAppBarController: TopAppBarController,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    userPreferences: UserPreferences,
    navController: NavController,
    requestedForUserRatingChanges: Boolean,
    toggleRequestedForUserRatingChangesTo: (Boolean) -> Unit
) {

    composable(route = Screens.ContestsScreen.name) {
        topAppBarController.title = Screens.ContestsScreen.title
        topAppBarController.expandToolbar = false

        val tabTitles =
            listOf(Screens.UpcomingContestsScreen.title, Screens.FinishedContestsScreen.title)
        val pagerState = rememberPagerState(initialPage = 0)
        val tabIndex = pagerState.currentPage

        var currentSelection by rememberSaveable {
            mutableStateOf(FinishedContestFilter.GIVEN)
        }
        topAppBarController.actions = {
            ContestScreenActions(
                currentSelectionForFinishedContests = currentSelection,
                onClickAll = { currentSelection = FinishedContestFilter.ALL },
                onClickGiven = { currentSelection = FinishedContestFilter.GIVEN }
            )
        }

        val requestForUserRatingChanges: () -> Unit = {
            if (!requestedForUserRatingChanges) {
                toggleRequestedForUserRatingChangesTo(true)
                coroutineScope.launch(Dispatchers.IO) {
                    userPreferences.handleNameFlow.collect { userHandle ->
                        mainViewModel.getUserRatingChanges(userHandle!!)
                    }
                }
            }
        }

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
                            CircularIndeterminateProgressBar(isDisplayed = true)
                        }
                    }
                    is ApiState.Success<*> -> {
                        if (apiResult.response.status == "OK") {
                            mainViewModel.contestListById.clear()
                            for (pair in mainViewModel.contestListsByPhase) {
                                pair.value.clear()
                            }
                            val contestList = apiResult.response.result as List<Contest>
                            for (contest in contestList) {
                                mainViewModel.contestListsByPhase[contest.phase]?.add(contest)
                                mainViewModel.contestListById[contest.id] = contest
                            }
                            val currentDate = getTodaysDate()
                            for (pair in mainViewModel.contestListBeforeByPhase) {
                                pair.value.clear()
                            }
                            for (contest in mainViewModel.contestListsByPhase[Phase.BEFORE]!!) {
                                val contestDate = contest.getContestDate()
                                when ((abs(contestDate.time - currentDate.time) / (24 * 3600 * 1000))) {
                                    0L, 1L, 2L -> mainViewModel.contestListBeforeByPhase[Phase.WITHIN_2DAYS]!!.add(
                                        contest
                                    )
                                    else -> mainViewModel.contestListBeforeByPhase[Phase.MORE]!!.add(
                                        contest
                                    )
                                }
                            }
                            when (index) {
                                0 -> {
                                    UpcomingContestScreen(
                                        contestLists = mainViewModel.contestListsByPhase,
                                        contestListBefore = mainViewModel.contestListBeforeByPhase,
                                    )
                                }
                                1 -> {
                                    Column {
                                        when (val apiResult =
                                            mainViewModel.responseForUserRatingChanges) {
                                            is ApiState.Loading -> {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularIndeterminateProgressBar(isDisplayed = true)
                                                }
                                            }
                                            is ApiState.Success<*> -> {
                                                val userRatingChanges =
                                                    apiResult.response.result as List<UserRatingChanges>
                                                userRatingChanges.forEachIndexed { index, userRatingChange ->
                                                    mainViewModel.contestListsByPhase[Phase.FINISHED]!!.find {
                                                        it.id == userRatingChange.contestId
                                                    }?.also {
                                                        it.isAttempted = true
                                                        it.ratingChange =
                                                            userRatingChange.newRating - userRatingChange.oldRating
                                                        it.rank = userRatingChange.rank
                                                        it.newRating = userRatingChange.newRating
                                                    }
                                                }
                                            }
                                            is ApiState.Failure -> {
                                                Row {
                                                    NormalButton(
                                                        text = "Retry",
                                                        onClick = {
                                                            toggleRequestedForUserRatingChangesTo(
                                                                false
                                                            )
                                                            requestForUserRatingChanges()
                                                        },
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                    )
                                                }
                                            }
                                            is ApiState.Empty -> {
                                                requestForUserRatingChanges()
                                            }
                                            else -> {
                                                // Nothing
                                            }
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
                    else -> {
                        // Nothing
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
fun Modifier.Companion.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it }
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffset
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.width, nextTab.width, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
        } else {
            currentTab.width.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}