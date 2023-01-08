package com.theruralguys.competrace.ui.navigation.navsections

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
import com.theruralguys.competrace.data.UserPreferences
import com.theruralguys.competrace.model.User
import com.theruralguys.competrace.retrofit.util.ApiState
import com.theruralguys.competrace.ui.components.CircularIndeterminateProgressBar
import com.theruralguys.competrace.ui.components.ProgressScreenActions
import com.theruralguys.competrace.ui.components.UserSubmissionsScreenActions
import com.theruralguys.competrace.ui.controllers.TopAppBarController
import com.theruralguys.competrace.ui.navigation.Screens
import com.theruralguys.competrace.ui.screens.NetworkFailScreen
import com.theruralguys.competrace.ui.screens.ProgressScreen
import com.theruralguys.competrace.ui.screens.UserSubmissionScreen
import com.theruralguys.competrace.utils.UserSubmissionFilter
import com.theruralguys.competrace.utils.processSubmittedProblemFromAPI
import com.theruralguys.competrace.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.progress(
    topAppBarController: TopAppBarController,
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel,
    userPreferences: UserPreferences,
    navController: NavController,
    requestedForUserInfo: Boolean,
    toggleRequestedForUserInfoTo: (Boolean) -> Unit,
    requestedForUserSubmission: Boolean,
    toggleRequestedForUserSubmissionTo: (Boolean) -> Unit,
    navigateToLoginActivity: () -> Unit,
    navigateToSettingsActivity: () -> Unit,
) {

    composable(route = Screens.ProgressScreen.name) {
        topAppBarController.title = Screens.ProgressScreen.title
        topAppBarController.expandToolbar = false

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

        val onClickSettings: () -> Unit = {
            coroutineScope.launch {
                navigateToSettingsActivity()
            }
        }

        val onClickLogoutBtn: () -> Unit = {
            coroutineScope.launch {
                userPreferences.setHandleName("")
                navigateToLoginActivity()
            }
        }

        val onClickRefresh: () -> Unit = {
            toggleRequestedForUserInfoTo(false)
            requestForUserInfo()
        }
        topAppBarController.actions = {
            ProgressScreenActions(
               onClickSettings = onClickSettings,
                onClickLogOut = onClickLogoutBtn,
            onClickRefresh = onClickRefresh)
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
        topAppBarController.title = Screens.UserSubmissionsScreen.title
        topAppBarController.expandToolbar = false

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

        topAppBarController.actions = {
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
                    processSubmittedProblemFromAPI(mainViewModel = mainViewModel, apiResult = apiResult)

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