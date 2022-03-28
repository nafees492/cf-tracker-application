package com.example.cfprogresstracker.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.model.Problem
import com.example.cfprogresstracker.model.Submission
import com.example.cfprogresstracker.model.User
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.NetworkFailScreen
import com.example.cfprogresstracker.ui.screens.ProgressScreen
import com.example.cfprogresstracker.ui.screens.UserSubmissionScreen
import com.example.cfprogresstracker.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.progress(
    toolbarController: ToolbarController,
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel,
    userPreferences: UserPreferences,
    navController: NavController,
    requestedForUserInfo: Boolean,
    toggleRequestedForUserInfoTo: (Boolean) -> Unit,
    requestedForUserSubmission: Boolean,
    toggleRequestedForUserSubmissionTo: (Boolean) -> Unit,
    navigateToLoginActivity: () -> Unit
) {

    composable(route = Screens.ProgressScreen.name) {
        toolbarController.title = Screens.ProgressScreen.title

        val onClickLogoutBtn: () -> Unit = {
            coroutineScope.launch {
                userPreferences.setHandleName("")
                navigateToLoginActivity()
            }
        }

        if (!requestedForUserInfo) {
            toggleRequestedForUserInfoTo(true)
            coroutineScope.launch(Dispatchers.IO) {
                userPreferences.handleNameFlow.collect { userHandle ->
                    mainViewModel.getUserInfo(userHandle!!)
                }
            }
        }

        when (val apiResult = mainViewModel.responseForUserInfo) {
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
                    mainViewModel.user = apiResult.response.result?.get(0) as User
                    mainViewModel.user?.let {
                        ProgressScreen(
                            user = it,
                            goToSubmission = { navController.navigate(Screens.UserSubmissionsScreen.name) },
                            onClickLogoutBtn = onClickLogoutBtn
                        )
                    }
                } else {
                    navigateToLoginActivity()
                }
            }
            is ApiState.Failure -> {
                NetworkFailScreen(onClickRetry = { toggleRequestedForUserInfoTo(false) })
            }
            is ApiState.Empty -> {}
        }
    }



    composable(Screens.UserSubmissionsScreen.name) {
        toolbarController.title = Screens.UserSubmissionsScreen.title

        if (!requestedForUserSubmission) {
            toggleRequestedForUserSubmissionTo(true)
            coroutineScope.launch(Dispatchers.IO) {
                userPreferences.handleNameFlow.collect { userHandle ->
                    mainViewModel.getUserSubmission(userHandle!!)
                }
            }
        }

        when (val apiResult = mainViewModel.responseForUserSubmissions) {
            is ApiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularIndeterminateProgressBar(isDisplayed = true)
                }
            }
            is ApiState.Success<*> -> {
                mainViewModel.problemMapWithSubmissions.clear()

                val submissions = apiResult.response.result as List<Submission>
                for (submission in submissions) {
                    if (mainViewModel.problemMapWithSubmissions[submission.problem].isNullOrEmpty()) {
                        mainViewModel.problemMapWithSubmissions[submission.problem] =
                            mutableListOf(submission)
                    } else {
                        mainViewModel.problemMapWithSubmissions[submission.problem]?.add(submission)
                    }
                }
                val submittedProblem = remember { mutableListOf<Pair<Problem, List<Submission>>>() }
                for (problem in mainViewModel.problemMapWithSubmissions) {
                    submittedProblem.add(Pair(problem.key, problem.value))
                }
                UserSubmissionScreen(submittedProblems = submittedProblem)
            }
            is ApiState.Failure -> {
                NetworkFailScreen(onClickRetry = { toggleRequestedForUserSubmissionTo(false) })
            }
            is ApiState.Empty -> {}
        }
    }
}