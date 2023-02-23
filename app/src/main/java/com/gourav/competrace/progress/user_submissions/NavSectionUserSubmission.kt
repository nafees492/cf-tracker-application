package com.gourav.competrace.progress.user_submissions

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.presentation.SharedViewModel
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsScreen
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsScreenActions
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.ui.components.SearchAppBar
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.utils.UserSubmissionFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.userSubmission(
    sharedViewModel: SharedViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    userPreferences: UserPreferences,
) {
    val topAppBarController = sharedViewModel.topAppBarController

    composable(route = Screens.UserSubmissionsScreen.name) {

        val contestListById by userSubmissionsViewModel.contestListById.collectAsState()

        topAppBarController.apply {
            screenTitle = Screens.UserSubmissionsScreen.title
            isTopAppBarExpanded = false
        }

        val coroutineScope = rememberCoroutineScope()

        val searchQuery by userSubmissionsViewModel.searchQuery.collectAsState()
        val searchBarFocusRequester = remember { FocusRequester() }

        BackHandler(topAppBarController.isSearchWidgetOpen) {
            topAppBarController.isSearchWidgetOpen = false
        }

        LaunchedEffect(!topAppBarController.isSearchWidgetOpen) {
            userSubmissionsViewModel.updateSearchQuery("")
        }

        topAppBarController.searchWidgetContent = {
            SearchAppBar(
                query = searchQuery,
                onValueChange = userSubmissionsViewModel::updateSearchQuery,
                onCloseClicked = {
                    topAppBarController.isSearchWidgetOpen = false
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
                onClickAll = {
                    userSubmissionsViewModel.updateCurrentSelection(UserSubmissionFilter.ALL)
                },
                onClickCorrect = {
                    userSubmissionsViewModel.updateCurrentSelection(UserSubmissionFilter.CORRECT)
                },
                onClickIncorrect = {
                    userSubmissionsViewModel.updateCurrentSelection(UserSubmissionFilter.INCORRECT)
                }
            )
        }

        val isRefreshing by userSubmissionsViewModel.isUserSubmissionRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        val showTags by userPreferences.showTagsFlow.collectAsState(initial = true)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                userSubmissionsViewModel.requestForUserSubmission(
                    userPreferences = userPreferences,
                    isForced = true
                )
            },
        ) {
            when (val apiState = userSubmissionsViewModel.responseForUserSubmissions) {
                is ApiState.Empty -> {
                    userSubmissionsViewModel.requestForUserSubmission(
                        userPreferences = userPreferences,
                        isForced = false
                    )
                }
                is ApiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(
                        onClickRetry = {
                            userSubmissionsViewModel.requestForUserSubmission(
                                userPreferences = userPreferences,
                                isForced = true
                            )
                        }
                    )
                }
                else /* ApiState.Success */ -> {
                    val filteredProblemsWithSubmission by
                    userSubmissionsViewModel.filteredProblemsWithSubmissions.collectAsState()

                    UserSubmissionsScreen(
                        submittedProblemsWithSubmissions = filteredProblemsWithSubmission,
                        codeforcesContestListById = contestListById,
                        showTags = showTags
                    )
                }
            }
        }
    }
}