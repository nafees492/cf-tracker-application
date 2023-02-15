package com.gourav.competrace.ui.navigation.navsections

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.progress.user.model.User
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.login.LoginActivity
import com.gourav.competrace.app_core.MainActivity
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsScreenActions
import com.gourav.competrace.ui.components.ProgressScreenActions
import com.gourav.competrace.ui.components.SearchAppBar
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.ui.screens.UserSubmissionsScreen
import com.gourav.competrace.ui.screens.ProgressScreen
import com.gourav.competrace.utils.UserSubmissionFilter
import com.gourav.competrace.utils.findActivity
import com.gourav.competrace.app_core.ApplicationViewModel
import com.gourav.competrace.app_core.MainViewModel
import com.gourav.competrace.progress.user_submissions.UserSubmissionsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
fun NavGraphBuilder.progress(
    applicationViewModel: ApplicationViewModel,
    mainViewModel: MainViewModel,
    userPreferences: UserPreferences,
    navController: NavController
) {
    val topAppBarController = applicationViewModel.topAppBarController

    composable(route = Screens.ProgressScreen.name) {
        topAppBarController.apply {
            screenTitle = Screens.ProgressScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

        val context = LocalContext.current
        val parentActivity = context.findActivity()
        val coroutineScope = rememberCoroutineScope()

        val navigateToLoginActivity: () -> Unit = {
            val intent = Intent(parentActivity, LoginActivity::class.java)
            Log.d(MainActivity.TAG, intent.toString())
            parentActivity.startActivity(intent).also {
                parentActivity.finish()
            }
        }

        val isSettingsDialogueOpen by applicationViewModel.isSettingsDialogueOpen.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = applicationViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        val onClickLogoutBtn: () -> Unit = {
            coroutineScope.launch {
                navigateToLoginActivity()
                userPreferences.setHandleName("")
            }
        }

        topAppBarController.actions = {
            ProgressScreenActions(
                onClickSettings = applicationViewModel::openSettingsDialog,
                onClickLogOut = onClickLogoutBtn
            )
        }

        val isRefreshing by mainViewModel.isUserRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                mainViewModel.requestForUserInfo(
                    userPreferences = userPreferences,
                    isForced = true
                )
            },
        ) {
            when (val apiState = mainViewModel.responseForUserInfo) {
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
                is ApiState.Success<*> -> {
                    if (apiState.response.status == "OK") {
                        mainViewModel.user = apiState.response.result?.get(0) as User

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
                                isForced = true
                            )
                        }
                    )
                }
                is ApiState.Empty -> {
                    mainViewModel.requestForUserInfo(
                        userPreferences = userPreferences,
                        isForced = false
                    )
                }
                else -> {}
            }
        }
    }

    composable(Screens.UserSubmissionsScreen.name) {

        val userSubmissionsViewModel: UserSubmissionsViewModel = viewModel()

        topAppBarController.apply {
            screenTitle = Screens.UserSubmissionsScreen.title
            isTopAppBarExpanded = false
        }

        val coroutineScope = rememberCoroutineScope()

        var searchQuery by remember {
            mutableStateOf("")
        }
        val searchBarFocusRequester = remember { FocusRequester() }

        BackHandler(topAppBarController.isSearchWidgetOpen) {
            topAppBarController.isSearchWidgetOpen = false
            searchQuery = ""
        }

        topAppBarController.searchWidgetContent = {
            SearchAppBar(
                query = searchQuery,
                onValueChange = { searchQuery = it },
                onCloseClicked = {
                    topAppBarController.isSearchWidgetOpen = false
                    searchQuery = ""
                },
                modifier = Modifier.focusRequester(searchBarFocusRequester),
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

        val currentSelection by userSubmissionsViewModel.currentSelection.collectAsState()

        topAppBarController.actions = {
            UserSubmissionsScreenActions(
                currentSelectionForUserSubmissions = currentSelection,
                onClickSearch = onClickSearch,
                onClickAll = { userSubmissionsViewModel.updateCurrentSelection(UserSubmissionFilter.ALL) },
                onClickCorrect = { userSubmissionsViewModel.updateCurrentSelection(UserSubmissionFilter.CORRECT) },
                onClickIncorrect = {
                    userSubmissionsViewModel.updateCurrentSelection(
                        UserSubmissionFilter.INCORRECT
                    )
                }
            )
        }

        val isRefreshing by mainViewModel.isUserSubmissionRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        val showTags by userPreferences.showTagsFlow.collectAsState(initial = true)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                mainViewModel.requestForUserSubmission(
                    userPreferences = userPreferences,
                    isForced = true
                )
            },
        ) {
            when (val apiState = mainViewModel.responseForUserSubmissions) {
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
                is ApiState.Success<*> -> {
                    if (apiState.response.status == "OK") {

                        val filteredProblemsWithSubmission = if (searchQuery.isBlank()) {
                            when (currentSelection) {
                                UserSubmissionFilter.ALL -> mainViewModel.submittedProblems
                                UserSubmissionFilter.CORRECT -> mainViewModel.correctProblems
                                else -> mainViewModel.incorrectProblems
                            }
                        } else {
                            mainViewModel.submittedProblems.filter {
                                val isProblemMatched =
                                    it.first.name.contains(searchQuery, ignoreCase = true)
                                val isContestMatched =
                                    mainViewModel.contestListById[it.first.contestId]?.name
                                        .toString().contains(searchQuery, ignoreCase = true)
                                isProblemMatched || isContestMatched
                            }
                        }

                        UserSubmissionsScreen(
                            submittedProblemsWithSubmissions = filteredProblemsWithSubmission,
                            contestListById = mainViewModel.contestListById,
                            showTags = showTags
                        )
                    } else {
                        mainViewModel.responseForUserSubmissions =
                            ApiState.Failure(Throwable(apiState.response.comment))
                    }
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(
                        onClickRetry = {
                            mainViewModel.requestForUserSubmission(
                                userPreferences = userPreferences,
                                isForced = true
                            )
                        }
                    )
                }
                is ApiState.Empty -> {
                    mainViewModel.requestForUserSubmission(
                        userPreferences = userPreferences,
                        isForced = false
                    )
                }
                else -> {}
            }
        }
    }
}