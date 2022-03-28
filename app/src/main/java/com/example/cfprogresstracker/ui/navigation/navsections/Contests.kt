package com.example.cfprogresstracker.ui.navigation.navsections

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.controllers.SearchWidgetState
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.ContestsScreen
import com.example.cfprogresstracker.ui.screens.FinishedContestScreen
import com.example.cfprogresstracker.ui.screens.NetworkFailScreen
import com.example.cfprogresstracker.viewmodel.MainViewModel
import com.example.cfprogresstracker.viewmodel.Phase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs

@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.contests(
    toolbarController: ToolbarController,
    mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    userPreferences: UserPreferences,
    navController: NavController,
    requestedForUserSubmission: Boolean,
    toggleRequestedForUserSubmissionTo: (Boolean) -> Unit
) {

    composable(route = Screens.ContestsScreen.name) {
        toolbarController.title = Screens.ContestsScreen.title
        toolbarController.clearActions()
        toolbarController.searchWidgetState = SearchWidgetState.Closed()

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
                        contest.startTimeSeconds?.let { time ->
                            val contestDate = getContestDate(time.toLong())
                            when ((abs(contestDate.time - currentDate.time) / (24 * 3600 * 1000))) {
                                0L, 1L, 2L -> mainViewModel.contestListBeforeByPhase[Phase.WITHIN_2DAYS]!!.add(
                                    contest
                                )
                                else -> mainViewModel.contestListBeforeByPhase[Phase.MORE]!!.add(
                                    contest
                                )
                            }
                        }
                    }

                    ContestsScreen(
                        contestLists = mainViewModel.contestListsByPhase,
                        contestListBefore = mainViewModel.contestListBeforeByPhase,
                        toolBarScrollBehavior = toolbarController.scrollBehavior,
                        onClickFinishedContests = { navController.navigate(Screens.FinishedContestsScreen.name) }
                    )
                } else {
                    mainViewModel.responseForContestList = ApiState.Failure(Throwable())
                }
            }
            is ApiState.Failure -> {
                NetworkFailScreen(onClickRetry = { mainViewModel.getContestList() })
            }
            is ApiState.Empty -> {}
        }
    }

    composable(Screens.FinishedContestsScreen.name) {
        toolbarController.title = Screens.FinishedContestsScreen.title

        val onSearchClicked: (String) -> Unit = {
            Log.d("Search Clicked", it)
        }
        val onCloseClicked: () -> Unit = {
            toolbarController.searchWidgetState = SearchWidgetState.Closed()
        }

        if (toolbarController.searchWidgetState is SearchWidgetState.Opened) toolbarController.clearActions()
        else toolbarController.actions = {
            IconButton(onClick = {
                toolbarController.searchWidgetState = SearchWidgetState.Opened(
                    onSearchClicked, onCloseClicked
                )
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        }

        if (!requestedForUserSubmission) {
            toggleRequestedForUserSubmissionTo(true)
            coroutineScope.launch(Dispatchers.IO) {
                userPreferences.handleNameFlow.collect { userHandle ->
                    mainViewModel.getUserSubmission(userHandle!!)
                }
            }
        }

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalArrangement = Arrangement.Center

            ) {
                when (val apiResult = mainViewModel.responseForUserSubmissions) {
                    is ApiState.Loading -> {
                        CircularIndeterminateProgressBar(isDisplayed = true)
                    }
                    is ApiState.Success<*> -> {
                        val submissions = apiResult.response.result as List<Submission>
                        for (submission in submissions) {
                            submission.contestId?.let {
                                mainViewModel.contestListById[it]?.hasSubmissions = true
                            }
                        }
                    }
                    is ApiState.Failure -> {
                        ClickableText(
                            text = AnnotatedString("Retry!"),
                            onClick = { toggleRequestedForUserSubmissionTo(false) },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    is ApiState.Empty -> {}
                }
            }
            FinishedContestScreen(finishedContests = mainViewModel.contestListsByPhase[Phase.FINISHED]!!)
        }

    }
}

private fun getContestDate(timeStamp: Long) = Date(timeStamp * 1000)
private fun getTodaysDate() = Date()
