package com.gourav.competrace.problemset

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.presentation.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.MyCircularProgressIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.problemset.presentation.ProblemSetScreen
import com.gourav.competrace.problemset.presentation.ProblemSetScreenActions
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.ui.components.RatingRangeSlider
import com.gourav.competrace.ui.components.SearchAppBar
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.screens.NetworkFailScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
fun NavGraphBuilder.problemSet(
    sharedViewModel: SharedViewModel,
    problemSetViewModel: ProblemSetViewModel,
    userPreferences: UserPreferences,
) {
    val topAppBarController = sharedViewModel.topAppBarController

    composable(route = Screens.ProblemSetScreen.name) {
        topAppBarController.apply {
            screenTitle = Screens.ProblemSetScreen.title
        }

        val coroutineScope = rememberCoroutineScope()

        val contestListById by problemSetViewModel.contestListById.collectAsState()

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()
        val ratingRangeValue by problemSetViewModel.ratingRangeValue.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = sharedViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        topAppBarController.expandedTopAppBarContent = {
            RatingRangeSlider(
                start = ratingRangeValue.first, end = ratingRangeValue.last,
                updateStartAndEnd = problemSetViewModel::updateRatingRange
            )
        }

        val searchQuery by problemSetViewModel.searchQuery.collectAsState()

        val searchBarFocusRequester = remember { FocusRequester() }

        BackHandler(topAppBarController.isSearchWidgetOpen) {
            topAppBarController.isSearchWidgetOpen = false
        }

        LaunchedEffect(!topAppBarController.isSearchWidgetOpen) {
            problemSetViewModel.updateSearchQuery("")
        }

        topAppBarController.searchWidgetContent = {
            SearchAppBar(
                query = searchQuery,
                onValueChange = problemSetViewModel::updateSearchQuery,
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

        val onClickFilterIcon: () -> Unit = {
            topAppBarController.isTopAppBarExpanded = !topAppBarController.isTopAppBarExpanded
        }

        topAppBarController.actions = {
            ProblemSetScreenActions(
                onClickSearch = onClickSearch,
                onClickSettings = sharedViewModel::openSettingsDialog,
                onClickFilterIcon = onClickFilterIcon,
                isToolbarExpanded = topAppBarController.isTopAppBarExpanded,
                ratingRange = ratingRangeValue
            )
        }

        val isRefreshing by problemSetViewModel.isProblemSetRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        val showTagsInProblemSet by userPreferences.showTagsFlow.collectAsState(initial = true)

        var selectedTabIndex by rememberSaveable {
            mutableStateOf(0)
        }

        val tabTitles = listOf("CodeForces")

        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        Column {
            AnimatedVisibility(visible = isPlatformTabRowVisible, modifier = Modifier.fillMaxWidth()) {
                CompetracePlatformRow(
                    selectedTabIndex = selectedTabIndex,
                    tabTitles = tabTitles,
                    onClickTab = { selectedTabIndex = it }
                )
            }
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = problemSetViewModel::refreshProblemSet,
            ) {
                when (problemSetViewModel.responseForProblemSet) {
                    is ApiState.Empty -> {}
                    is ApiState.Loading -> {
                        MyCircularProgressIndicator(
                            isDisplayed = true,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(onClickRetry = { problemSetViewModel.getProblemSet() })
                    }
                    is ApiState.Success -> {
                        val filteredProblems by problemSetViewModel.filteredProblems.collectAsState()

                        ProblemSetScreen(
                            listOfCodeforcesProblem = filteredProblems,
                            codeforcesContestListById = contestListById,
                            tagList = problemSetViewModel.tagList,
                            showTags = showTagsInProblemSet
                        )
                    }
                }
            }
        }
    }
}