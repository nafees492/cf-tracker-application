package com.theruralguys.competrace.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.theruralguys.competrace.data.UserPreferences
import com.theruralguys.competrace.model.User
import com.theruralguys.competrace.retrofit.util.ApiState
import com.theruralguys.competrace.ui.components.ProblemSubmissionsScreenActions
import com.theruralguys.competrace.ui.components.ProgressScreenActions
import com.theruralguys.competrace.ui.components.SettingsAlertDialog
import com.theruralguys.competrace.ui.controllers.TopAppBarController
import com.theruralguys.competrace.ui.navigation.Screens
import com.theruralguys.competrace.ui.screens.NetworkFailScreen
import com.theruralguys.competrace.ui.screens.ProblemSubmissionScreen
import com.theruralguys.competrace.ui.screens.ProgressScreen
import com.theruralguys.competrace.utils.UserSubmissionFilter
import com.theruralguys.competrace.utils.processSubmittedProblemFromAPIResult
import com.theruralguys.competrace.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.progress(
    topAppBarController: TopAppBarController,
    coroutineScope: CoroutineScope,
    mainViewModel: MainViewModel,
    userPreferences: UserPreferences,
    navController: NavController,
    navigateToLoginActivity: () -> Unit,
) {

    composable(route = Screens.ProgressScreen.name) {
        topAppBarController.title = Screens.ProgressScreen.title
        topAppBarController.expandToolbar = false

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

        val onClickSettings: () -> Unit = { isSettingsDialogueOpen = true }

        val onClickLogoutBtn: () -> Unit = {
            coroutineScope.launch {
                userPreferences.setHandleName("")
                navigateToLoginActivity()
            }
        }

        topAppBarController.actions = {
            ProgressScreenActions(
                onClickSettings = onClickSettings,
                onClickLogOut = onClickLogoutBtn
            )
        }

        val isRefreshing = mainViewModel.isUserRefreshing.collectAsState().value
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                mainViewModel.requestForUserInfo(
                    userPreferences = userPreferences,
                    isRefreshed = true
                )
            },
        ) {
            when (val apiResult = mainViewModel.responseForUserInfo) {
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {}
                }
                is ApiState.Success<*> -> {
                    if (apiResult.response.status == "OK") {
                        mainViewModel.user = apiResult.response.result?.get(0) as User

                        mainViewModel.user?.let {
                            ProgressScreen(
                                user = it,
                                goToSubmission = { navController.navigate(Screens.UserSubmissionsScreen.name) },
                                mainViewModel = mainViewModel,
                                userPreferences = userPreferences
                            )
                        }
                    } else {
                        navigateToLoginActivity()
                    }
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(
                        onClickRetry = {
                            mainViewModel.requestForUserInfo(
                                userPreferences = userPreferences,
                                isRefreshed = true
                            )
                        }
                    )
                }
                is ApiState.Empty -> {
                    mainViewModel.requestForUserInfo(
                        userPreferences = userPreferences,
                        isRefreshed = false
                    )
                }
                else -> {}
            }
        }
    }

    composable(Screens.UserSubmissionsScreen.name) {
        topAppBarController.title = Screens.UserSubmissionsScreen.title
        topAppBarController.expandToolbar = false

        var currentSelection by rememberSaveable {
            mutableStateOf(UserSubmissionFilter.ALL)
        }

        topAppBarController.actions = {
            ProblemSubmissionsScreenActions(
                currentSelectionForUserSubmissions = currentSelection,
                onClickAll = { currentSelection = UserSubmissionFilter.ALL },
                onClickCorrect = { currentSelection = UserSubmissionFilter.CORRECT },
                onClickIncorrect = { currentSelection = UserSubmissionFilter.INCORRECT }
            )
        }

        val isRefreshing = mainViewModel.isUserSubmissionRefreshing.collectAsState().value
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                mainViewModel.requestForUserSubmission(
                    userPreferences = userPreferences,
                    isRefreshed = true
                )

            },
        ) {
            when (val apiResult = mainViewModel.responseForUserSubmissions) {
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {}
                }
                is ApiState.Success<*> -> {
                    if (apiResult.response.status == "OK") {
                        processSubmittedProblemFromAPIResult(
                            mainViewModel = mainViewModel,
                            apiResult = apiResult
                        )

                        ProblemSubmissionScreen(
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
                            mainViewModel.requestForUserSubmission(
                                userPreferences = userPreferences,
                                isRefreshed = true
                            )

                        }
                    )
                }
                is ApiState.Empty -> {
                    mainViewModel.requestForUserSubmission(
                        userPreferences = userPreferences,
                        isRefreshed = false
                    )
                }
                else -> {}
            }
        }
    }
}