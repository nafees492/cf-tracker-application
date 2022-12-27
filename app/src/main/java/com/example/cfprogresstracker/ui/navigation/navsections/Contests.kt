package com.example.cfprogresstracker.ui.navigation.navsections

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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.UserRatingChanges
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.components.ContestScreenActions
import com.example.cfprogresstracker.ui.components.NormalButton
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.*
import com.example.cfprogresstracker.utils.FinishedContestFilter
import com.example.cfprogresstracker.utils.Phase
import com.example.cfprogresstracker.utils.getTodaysDate
import com.example.cfprogresstracker.utils.pagerTabIndicatorOffset
import com.example.cfprogresstracker.viewmodel.MainViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi // 1.
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    toolbarController: ToolbarController,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    userPreferences: UserPreferences,
    navController: NavController,
    requestedForUserRatingChanges: Boolean,
    toggleRequestedForUserRatingChangesTo: (Boolean) -> Unit
) {

    composable(route = Screens.ContestsScreen.name) {
        toolbarController.title = Screens.ContestsScreen.title
        toolbarController.expandToolbar = false

        val tabTitles =
            listOf(Screens.UpcomingContestsScreen.title, Screens.FinishedContestsScreen.title)
        val pagerState = rememberPagerState(initialPage = 0)
        val tabIndex = pagerState.currentPage

        var currentSelection by rememberSaveable {
            mutableStateOf(FinishedContestFilter.GIVEN)
        }
        toolbarController.actions = {
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

        Column() {
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
                                                Row(){
                                                    NormalButton(
                                                        text = "Retry",
                                                        onClick = {
                                                            toggleRequestedForUserRatingChangesTo(false)
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
                                            mainViewModel.contestListsByPhase[Phase.FINISHED]!! as ArrayList<Contest>
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
