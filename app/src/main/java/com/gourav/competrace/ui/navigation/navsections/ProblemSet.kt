package com.gourav.competrace.ui.navigation.navsections

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.model.Problem
import com.gourav.competrace.retrofit.util.ApiState
import com.gourav.competrace.ui.components.*
import com.gourav.competrace.ui.controllers.TopAppBarController
import com.gourav.competrace.ui.navigation.Screens
import com.gourav.competrace.ui.screens.NetworkFailScreen
import com.gourav.competrace.ui.screens.ProblemSetScreen
import com.gourav.competrace.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
fun NavGraphBuilder.problemSet(
    topAppBarController: TopAppBarController,
    mainViewModel: MainViewModel,
    userPreferences: UserPreferences,
) {
    composable(route = Screens.ProblemSetScreen.name) {
        topAppBarController.title = Screens.ProblemSetScreen.title

        val coroutineScope = rememberCoroutineScope()

        var startRatingValue by rememberSaveable {
            mutableStateOf(800)
        }
        var endRatingValue by rememberSaveable {
            mutableStateOf(3500)
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

        topAppBarController.expandedTopAppBarContent = {
            RatingRangeSlider(
                start = startRatingValue, end = endRatingValue,
                updateStartAndEnd = { start, end ->
                    startRatingValue = start
                    endRatingValue = end
                }
            )
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
                onClickSettings = { isSettingsDialogueOpen = true },
                onClickFilterIcon = onClickFilterIcon,
                isToolbarExpanded = topAppBarController.isTopAppBarExpanded,
                ratingRange = startRatingValue..endRatingValue
            )
        }

        val isRefreshing = mainViewModel.isProblemSetRefreshing.collectAsState().value
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = mainViewModel::refreshProblemSet,
        ) {
            when (val apiResult = mainViewModel.responseForProblemSet) {
                is ApiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(isDisplayed = true)
                    }
                }
                is ApiState.SuccessPS -> {
                    if (apiResult.response.status == "OK") {
                        val allProblems: ArrayList<Problem> =
                            apiResult.response.result!!.problems as ArrayList<Problem>

                        val setOfTags = mutableSetOf<String>()
                        allProblems.forEach { problem ->
                            problem.tags?.forEach {
                                setOfTags.add(it)
                            }
                        }
                        mainViewModel.tagList.clear()
                        mainViewModel.tagList.addAll(setOfTags)

                        val filteredProblemListByRatingAndSearch = arrayListOf<Problem>()

                        allProblems.forEach {
                            it.rating?.let { rating ->
                                val problemName = it.name.lowercase()
                                val contestName = mainViewModel.contestListById[it.contestId]?.name?.lowercase() ?: ""
                                if (rating in startRatingValue..endRatingValue) {
                                    if (searchQuery.isBlank()
                                        || problemName.contains(searchQuery.lowercase())
                                        || contestName.contains(searchQuery.lowercase())
                                    ) {
                                        filteredProblemListByRatingAndSearch.add(it)
                                    }
                                }
                            }
                        }

                        ProblemSetScreen(
                            listOfProblem = filteredProblemListByRatingAndSearch,
                            contestListById = mainViewModel.contestListById,
                            tagList = mainViewModel.tagList
                        )
                    } else {
                        mainViewModel.responseForProblemSet = ApiState.Failure(Throwable())
                    }
                }
                is ApiState.Failure -> {
                    NetworkFailScreen(onClickRetry = { mainViewModel.getProblemSet() })
                }
                is ApiState.Empty -> {}
                else -> {
                    // Nothing
                }
            }
        }
    }
}