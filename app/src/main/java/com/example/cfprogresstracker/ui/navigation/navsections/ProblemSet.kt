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
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.NetworkFailScreen
import com.example.cfprogresstracker.ui.screens.ProblemSetScreen
import com.example.cfprogresstracker.utils.ProblemSetFilter
import com.example.cfprogresstracker.viewmodel.MainViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.problemSet(
    toolbarController: ToolbarController,
    mainViewModel: MainViewModel,

) {

    composable(route = Screens.ProblemSetScreen.name) {
        toolbarController.title = Screens.ProblemSetScreen.title

        var currentSelection by rememberSaveable {
            mutableStateOf(ProblemSetFilter.ALL)
        }

        val onClickFilters : ArrayList<() -> Unit> = ArrayList()
        for(i in 0..27) onClickFilters.add { currentSelection = ProblemSetFilter.RATING[i] }
        toolbarController.actions = {
            ProblemSetScreenActions(
                currentSelectionForProblemSet = currentSelection,
                onClickAll = { currentSelection = ProblemSetFilter.ALL },
                onClick = onClickFilters
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
                    val filteredProblemList =
                        if (currentSelection == ProblemSetFilter.ALL) {
                            allProblems
                        }
                        else {
                            val resultList = ArrayList<Problem>()
                            allProblems.forEach {
                                it.rating?.let { rating ->
                                    if(rating == currentSelection.toInt()) resultList.add(it)
                                }
                            }
                            resultList
                        }
                    ProblemSetScreen(listOfProblem = filteredProblemList, contestListById = mainViewModel.contestListById)
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