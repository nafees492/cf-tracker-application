package com.gourav.competrace.ui.navigation.navsections

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.ui.components.*
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.ui.screens.ProblemSetScreen
import com.gourav.competrace.app_core.ApplicationViewModel
import com.gourav.competrace.app_core.MainViewModel
import com.gourav.competrace.problemset.ProblemSetViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
fun NavGraphBuilder.problemSet(
    applicationViewModel: ApplicationViewModel,
    mainViewModel: MainViewModel,
    userPreferences: UserPreferences,
) {
    val topAppBarController = applicationViewModel.topAppBarController

    composable(route = Screens.ProblemSetScreen.name) {
        topAppBarController.screenTitle = Screens.ProblemSetScreen.title

        val problemSetViewModel: ProblemSetViewModel = viewModel()
        val coroutineScope = rememberCoroutineScope()

        val isSettingsDialogueOpen by applicationViewModel.isSettingsDialogueOpen.collectAsState()
        val startRatingValue by problemSetViewModel.startRatingValue.collectAsState()
        val endRatingValue by problemSetViewModel.endRatingValue.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = applicationViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        topAppBarController.expandedTopAppBarContent = {
            RatingRangeSlider(
                start = startRatingValue, end = endRatingValue,
                updateStartAndEnd = problemSetViewModel::updateStartAndEnd
            )
        }

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

        val onClickFilterIcon: () -> Unit = {
            topAppBarController.isTopAppBarExpanded = !topAppBarController.isTopAppBarExpanded
        }

        topAppBarController.actions = {
            ProblemSetScreenActions(
                onClickSearch = onClickSearch,
                onClickSettings = applicationViewModel::openSettingsDialog,
                onClickFilterIcon = onClickFilterIcon,
                isToolbarExpanded = topAppBarController.isTopAppBarExpanded,
                ratingRange = startRatingValue..endRatingValue
            )
        }

        val isRefreshing by mainViewModel.isProblemSetRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        val showTagsInProblemSet by userPreferences.showTagsFlow.collectAsState(initial = true)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = mainViewModel::refreshProblemSet,
        ) {
            when (val apiResult = mainViewModel.responseForProblemSet) {
                is ApiState.Loading -> {
                    CircularProgressIndicator(isDisplayed = true, modifier = Modifier.fillMaxSize())
                }
                is ApiState.SuccessPS -> {
                    if (apiResult.response.status == "OK") {

                        val filteredProblems = mainViewModel.allProblems.filter {
                            val isProblemMatched = it.name.contains(searchQuery, ignoreCase = true)
                            val isContestMatched = mainViewModel.contestListById[it.contestId]?.name
                                .toString().contains(searchQuery, ignoreCase = true)
                            searchQuery.isBlank() || isProblemMatched || isContestMatched
                        }.filter {
                            val isDefault =
                                (it.rating == null && startRatingValue == 800 && endRatingValue == 3500)
                            isDefault || (it.rating in startRatingValue..endRatingValue)
                        }

                        ProblemSetScreen(
                            listOfProblem = filteredProblems,
                            contestListById = mainViewModel.contestListById,
                            tagList = mainViewModel.tagList,
                            showTags = showTagsInProblemSet
                        )
                    } else {
                        mainViewModel.responseForProblemSet = ApiState.Failure(Throwable())
                    }
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(onClickRetry = { mainViewModel.getProblemSet() })
                }
                else -> {}
            }
        }
    }
}