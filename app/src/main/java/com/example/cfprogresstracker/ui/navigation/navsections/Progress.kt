package com.example.cfprogresstracker.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.cfprogresstracker.ui.components.ProgressScreenActions
import com.example.cfprogresstracker.ui.components.UserSubmissionsScreenActions
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.screens.NetworkFailScreen
import com.example.cfprogresstracker.ui.screens.ProgressScreen
import com.example.cfprogresstracker.ui.screens.UserSubmissionScreen
import com.example.cfprogresstracker.utils.UserSubmissionFilter
import com.example.cfprogresstracker.utils.Verdict
import com.example.cfprogresstracker.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
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
    navigateToLoginActivity: () -> Unit,
) {

    composable(route = Screens.ProgressScreen.name) {
        toolbarController.title = Screens.ProgressScreen.title

        val onClickLogoutBtn: () -> Unit = {
            coroutineScope.launch {
                userPreferences.setHandleName("")
                navigateToLoginActivity()
            }
        }

        toolbarController.actions = {
            ProgressScreenActions(onClickLogOut = onClickLogoutBtn)
        }

        val requestForUserInfo: () -> Unit = {
            if (!requestedForUserInfo) {
                toggleRequestedForUserInfoTo(true)
                coroutineScope.launch(Dispatchers.IO) {
                    userPreferences.handleNameFlow.collect { userHandle ->
                        mainViewModel.getUserInfo(userHandle!!)
                    }
                }
            }
        }

        val requestForUserSubmission: () -> Unit = {
            if (!requestedForUserSubmission) {
                toggleRequestedForUserSubmissionTo(true)
                coroutineScope.launch(Dispatchers.IO) {
                    userPreferences.handleNameFlow.collect { userHandle ->
                        mainViewModel.getUserSubmission(userHandle!!)
                    }
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
                            mainViewModel = mainViewModel,
                            requestForUserSubmission = requestForUserSubmission,
                            toggleRequestedForUserSubmissionTo = toggleRequestedForUserSubmissionTo
                        )
                    }
                } else {
                    navigateToLoginActivity()
                }
            }
            is ApiState.Failure -> {
                NetworkFailScreen(
                    onClickRetry = {
                        toggleRequestedForUserInfoTo(false)
                        requestForUserInfo()
                    }
                )
            }
            is ApiState.Empty -> {
                requestForUserInfo()
            }
            else -> {
                // Nothing
            }
        }
    }

    composable(Screens.UserSubmissionsScreen.name) {
        toolbarController.title = Screens.UserSubmissionsScreen.title

        var currentSelection by rememberSaveable {
            mutableStateOf(UserSubmissionFilter.ALL)
        }

        val requestForUserSubmission: () -> Unit = {
            if (!requestedForUserSubmission) {
                toggleRequestedForUserSubmissionTo(true)
                coroutineScope.launch(Dispatchers.IO) {
                    userPreferences.handleNameFlow.collect { userHandle ->
                        mainViewModel.getUserSubmission(userHandle!!)
                    }
                }
            }
        }

        toolbarController.actions = {
            UserSubmissionsScreenActions(
                currentSelectionForUserSubmissions = currentSelection,
                onClickAll = { currentSelection = UserSubmissionFilter.ALL },
                onClickCorrect = { currentSelection = UserSubmissionFilter.CORRECT },
                onClickIncorrect = { currentSelection = UserSubmissionFilter.INCORRECT }
            )
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
                if(apiResult.response.status == "OK") {
                    processSubmittedProblem(mainViewModel = mainViewModel, apiResult = apiResult)

                    UserSubmissionScreen(
                        submittedProblemsWithSubmissions = when (currentSelection) {
                            UserSubmissionFilter.ALL -> mainViewModel.submittedProblems
                            UserSubmissionFilter.CORRECT -> mainViewModel.correctProblems
                            else -> mainViewModel.incorrectProblems
                        },
                        contestListById = mainViewModel.contestListById
                    )
                } else {
                    mainViewModel.responseForUserSubmissions = ApiState.Failure(Throwable())
                }
            }
            is ApiState.Failure -> {
                NetworkFailScreen(
                    onClickRetry = {
                        toggleRequestedForUserSubmissionTo(false)
                        requestForUserSubmission()
                    }
                )
            }
            is ApiState.Empty -> {
                requestForUserSubmission()
            }
            else -> {
                // Nothing
            }
        }
    }
}

fun processSubmittedProblem(mainViewModel: MainViewModel, apiResult: ApiState.Success<*>){
    mainViewModel.submittedProblems.clear()
    mainViewModel.incorrectProblems.clear()
    mainViewModel.correctProblems.clear()

    val problemNameMapWithSubmissions =
        mutableMapOf<String, ArrayList<Submission>>()
    val problemNameMapWithProblem = mutableMapOf<String, Problem>()
    val problemNameWithVerdictOK: MutableSet<String> = mutableSetOf()

    val submissions: ArrayList<Submission> =
        apiResult.response.result as ArrayList<Submission>

    submissions.forEach {
        if (it.verdict == Verdict.OK) problemNameWithVerdictOK.add(it.problem.name)
        problemNameMapWithProblem[it.problem.name] = it.problem
        if (problemNameMapWithSubmissions[it.problem.name].isNullOrEmpty()) {
            problemNameMapWithSubmissions[it.problem.name] = arrayListOf(it)
        } else {
            problemNameMapWithSubmissions[it.problem.name]?.add(it)
        }
    }

    problemNameWithVerdictOK.forEach {
        problemNameMapWithProblem[it]?.hasVerdictOK = true
    }

    for (problem in problemNameMapWithProblem) {
        Pair(problem.value, problemNameMapWithSubmissions[problem.key]!!).let {
            mainViewModel.submittedProblems.add(it)
            if (problem.value.hasVerdictOK) mainViewModel.correctProblems.add(it)
            else mainViewModel.incorrectProblems.add(it)
        }
    }
}

