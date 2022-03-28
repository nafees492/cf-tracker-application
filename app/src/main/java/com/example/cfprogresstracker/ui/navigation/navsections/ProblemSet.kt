package com.example.cfprogresstracker.ui.navigation.navsections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cfprogresstracker.model.Contest
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.ContestsScreen
import com.example.cfprogresstracker.ui.screens.NetworkFailScreen
import com.example.cfprogresstracker.ui.screens.ProblemSetScreen
import com.example.cfprogresstracker.viewmodel.MainViewModel
import com.example.cfprogresstracker.viewmodel.Phase
import kotlin.math.abs

fun NavGraphBuilder.problemSet(
    toolbarController: ToolbarController,
    mainViewModel: MainViewModel,

) {

    composable(route = Screens.ProblemSetScreen.name) {
        toolbarController.clearActions()
        toolbarController.title = Screens.ProblemSetScreen.title

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
                    ProblemSetScreen(apiResult.response.result!!.problems)
                } else {
                    mainViewModel.responseForProblemSet = ApiState.Failure(Throwable())
                }
            }
            is ApiState.Failure -> {
                NetworkFailScreen(onClickRetry = { mainViewModel.getProblemSet() })
            }
            is ApiState.Empty -> {}
        }
    }
}