package com.gourav.competrace.problemset

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.problemset.presentation.ProblemSetScreen
import com.gourav.competrace.problemset.presentation.ProblemSetScreenActions
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.problemset.presentation.RatingRangeSlider
import com.gourav.competrace.ui.components.SearchAppBar
import com.gourav.competrace.settings.SettingsAlertDialog
import com.gourav.competrace.ui.screens.NetworkFailScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.gourav.competrace.R
import com.gourav.competrace.app_core.util.TopAppBarManager

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.problemSet(
    sharedViewModel: SharedViewModel,
    problemSetViewModel: ProblemSetViewModel,
) {
    composable(route = Screens.ProblemSetScreen.route) {
        val scope = rememberCoroutineScope()

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()
        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        val showTagsInProblemSet by problemSetViewModel.showTags.collectAsState(initial = true)

        val filteredProblems by problemSetViewModel.filteredProblems.collectAsState()
        val codeforcesContestListById by problemSetViewModel.codeforcesContestListById.collectAsState()
        val ratingRangeValue by problemSetViewModel.ratingRangeValue.collectAsState()
        val searchQuery by problemSetViewModel.searchQuery.collectAsState()
        val allTags by problemSetViewModel.allTags.collectAsState()
        val selectedChips by problemSetViewModel.selectedChips.collectAsState()

        val responseForProblemSet by problemSetViewModel.responseForProblemSet.collectAsState()
        val isRefreshing by problemSetViewModel.isProblemSetRefreshing.collectAsState()

        val searchBarFocusRequester = remember { FocusRequester() }

        val onClickSearch: () -> Unit = {
            TopAppBarManager.openSearchWidget()
            scope.launch {
                delay(100)
                searchBarFocusRequester.requestFocus()
            }
        }

        LaunchedEffect(Unit){
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ProblemSetScreen,
                expandedTopAppBarContent = {
                    RatingRangeSlider(
                        start = ratingRangeValue.first, end = ratingRangeValue.last,
                        updateStartAndEnd = problemSetViewModel::updateRatingRange
                    )
                },
                searchWidget = {
                    SearchAppBar(
                        query = searchQuery,
                        onValueChange = problemSetViewModel::updateSearchQuery,
                        onCloseClicked = TopAppBarManager::closeSearchWidget,
                        modifier = Modifier.focusRequester(searchBarFocusRequester),
                        placeHolderText = stringResource(id = R.string.search_problem_contest)
                    )
                },
                actions = {
                    ProblemSetScreenActions(
                        onClickSearch = onClickSearch,
                        onClickSettings = sharedViewModel::openSettingsDialog,
                        onClickFilterIcon = TopAppBarManager::toggleExpandedState,
                        badgeConditionForSearch = searchQuery.isNotBlank(),
                        badgeConditionForFilter = ratingRangeValue != 800..3500
                    )
                }
            )
        }

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = sharedViewModel::dismissSettingsDialog
        )

        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
        val tabTitles = listOf("CodeForces")

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
                onRefresh = problemSetViewModel::refreshProblemSetAndContests,
                indicator = CompetraceSwipeRefreshIndicator
            ) {
                when (responseForProblemSet) {
                    is ApiState.Empty -> {}
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(onClickRetry = problemSetViewModel::refreshProblemSetAndContests )
                    }
                    is ApiState.Success -> {
                        ProblemSetScreen(
                            problems = filteredProblems,
                            codeforcesContestListById = codeforcesContestListById,
                            allTags = allTags,
                            selectedChips = selectedChips,
                            updateSelectedChips = problemSetViewModel::updateSelectedChips,
                            clearSelectedChips = problemSetViewModel::clearSelectedChips,
                            showTags = showTagsInProblemSet
                        )
                    }
                }
            }
        }
    }
}