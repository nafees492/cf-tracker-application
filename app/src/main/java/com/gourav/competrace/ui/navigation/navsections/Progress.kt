package com.gourav.competrace.ui.navigation.navsections

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.model.Problem
import com.gourav.competrace.model.Submission
import com.gourav.competrace.model.User
import com.gourav.competrace.retrofit.util.ApiState
import com.gourav.competrace.ui.components.ProblemSubmissionsScreenActions
import com.gourav.competrace.ui.components.ProgressScreenActions
import com.gourav.competrace.ui.components.SearchAppBar
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.controllers.TopAppBarController
import com.gourav.competrace.ui.navigation.Screens
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.ui.screens.ProblemSubmissionScreen
import com.gourav.competrace.ui.screens.ProgressScreen
import com.gourav.competrace.utils.UserSubmissionFilter
import com.gourav.competrace.utils.processSubmittedProblemFromAPIResult
import com.gourav.competrace.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
        topAppBarController.apply {
            title = Screens.ProgressScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

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
        topAppBarController.apply {
            title = Screens.UserSubmissionsScreen.title
            isTopAppBarExpanded = false
        }


        var searchQuery by remember {
            mutableStateOf("")
        }
        val searchBarFocusRequester = remember { FocusRequester() }

        topAppBarController.searchWidgetContent = {
            SearchAppBar(
                query = searchQuery,
                onValueChange = { searchQuery = it },
                onCloseClicked = {
                    searchQuery = ""
                    topAppBarController.isSearchWidgetOpen = false
                },
                modifier =  Modifier.focusRequester(searchBarFocusRequester),
                placeHolderText = "Search Problem / Contest"
            )
        }

        val onClickSearch: () -> Unit = {
            topAppBarController.isSearchWidgetOpen = true
            coroutineScope.launch {
                delay(100)
                searchBarFocusRequester.requestFocus()
            }
        }

        var currentSelection by rememberSaveable {
            mutableStateOf(UserSubmissionFilter.ALL)
        }

        topAppBarController.actions = {
            ProblemSubmissionsScreenActions(
                currentSelectionForUserSubmissions = currentSelection,
                onClickSearch = onClickSearch,
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

                        val filteredSubmission = if (searchQuery.isBlank()) {
                            when (currentSelection) {
                                UserSubmissionFilter.ALL -> mainViewModel.submittedProblems
                                UserSubmissionFilter.CORRECT -> mainViewModel.correctProblems
                                else -> mainViewModel.incorrectProblems
                            }
                        } else {
                            val temp = arrayListOf<Pair<Problem, ArrayList<Submission>>>()
                            mainViewModel.submittedProblems.forEach {
                                val problemName = it.first.name.lowercase()
                                val contestName = mainViewModel.contestListById[it.first.contestId]?.name?.lowercase() ?: ""
                                if (problemName.contains(searchQuery.lowercase()) || contestName.contains(searchQuery.lowercase()))
                                    temp.add(it)
                            }
                            temp
                        }

                        ProblemSubmissionScreen(
                            submittedProblemsWithSubmissions = filteredSubmission,
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