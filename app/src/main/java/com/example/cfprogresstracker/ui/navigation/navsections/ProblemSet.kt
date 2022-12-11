package com.example.cfprogresstracker.ui.navigation.navsections

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.components.ProblemSetScreenActions
import com.example.cfprogresstracker.ui.components.RatingRangeSlider
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.NetworkFailScreen
import com.example.cfprogresstracker.ui.screens.ProblemSetScreen
import com.example.cfprogresstracker.viewmodel.MainViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.problemSet(
    toolbarController: ToolbarController,
    mainViewModel: MainViewModel,

    ) {

    composable(route = Screens.ProblemSetScreen.name) {
        toolbarController.title = Screens.ProblemSetScreen.title

        val onClickFilterIcon: () -> Unit = {
            toolbarController.expandToolbar = !toolbarController.expandToolbar
        }

        var startRatingValue by rememberSaveable {
            mutableStateOf(800)
        }
        var endRatingValue by rememberSaveable {
            mutableStateOf(3500)
        }

        toolbarController.expandedContent = {
            RatingRangeSlider(
                start = startRatingValue, end = endRatingValue,
                updateStartAndEnd = { start, end ->
                    startRatingValue = start
                    endRatingValue = end
                }
            )
        }

        toolbarController.actions = {
            ProblemSetScreenActions(
                onClickFilterIcon = onClickFilterIcon,
                isToolbarExpanded = toolbarController.expandToolbar,
                ratingRange = startRatingValue..endRatingValue
            )
        }



        when (val apiResult = mainViewModel.responseForProblemSet) {
            is ApiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularIndeterminateProgressBar(isDisplayed = true)
                }
            }
            is ApiState.SuccessPS -> {
                if (apiResult.response.status == "OK") {
                    val allProblems: ArrayList<Problem> =
                        apiResult.response.result!!.problems as ArrayList<Problem>

                    val setOfTags = mutableSetOf<String>()
                    allProblems.forEach { problem ->
                        problem.tags?.forEach {
                            setOfTags.add(it)
                        }
                    }
                    mainViewModel.tagList.clear()
                    mainViewModel.tagList.addAll(setOfTags)

                    val filteredProblemList = arrayListOf<Problem>()
                    allProblems.forEach {
                        it.rating?.let { rating ->
                            if (rating in startRatingValue..endRatingValue) filteredProblemList.add(it)
                        }
                    }

                    ProblemSetScreen(
                        listOfProblem = filteredProblemList,
                        contestListById = mainViewModel.contestListById,
                        tagList = mainViewModel.tagList
                    )
                } else {
                    mainViewModel.responseForProblemSet = ApiState.Failure(Throwable())
                }
            }
            is ApiState.Failure -> {
                NetworkFailScreen(onClickRetry = { mainViewModel.getProblemSet() })
            }
            is ApiState.Empty -> {}
            else -> {
                // Nothing
            }
        }
    }
}