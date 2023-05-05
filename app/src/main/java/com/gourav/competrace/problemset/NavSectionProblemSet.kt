package com.gourav.competrace.problemset

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.FailureScreen
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetracePullRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarManager
import com.gourav.competrace.problemset.presentation.ProblemSetScreen
import com.gourav.competrace.problemset.presentation.ProblemSetScreenActions
import com.gourav.competrace.problemset.presentation.ProblemSetViewModel
import com.gourav.competrace.problemset.presentation.RatingRangeSlider
import com.gourav.competrace.ui.components.SearchAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.problemSet(
    problemSetViewModel: ProblemSetViewModel,
    appState: CompetraceAppState,
    paddingValues: PaddingValues
) {
    composable(route = Screens.ProblemSetScreen.route) {
        val scope = rememberCoroutineScope()

        val isPlatformTabRowVisible by appState.isPlatformsTabRowVisible.collectAsStateWithLifecycle()
        val state by problemSetViewModel.screenState.collectAsStateWithLifecycle()

        val searchBarFocusRequester = remember { FocusRequester() }

        val onClickSearch: () -> Unit = {
            TopAppBarManager.openSearchWidget()
            scope.launch {
                delay(100)
                searchBarFocusRequester.requestFocus()
            }
        }

        LaunchedEffect(Unit) {
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ProblemSetScreen,
                searchWidget = {
                    SearchAppBar(
                        query = state.searchQuery,
                        onValueChange = problemSetViewModel::updateSearchQuery,
                        onCloseClicked = TopAppBarManager::closeSearchWidget,
                        modifier = Modifier.focusRequester(searchBarFocusRequester),
                        placeHolderText = stringResource(id = R.string.search_problem_contest)
                    )
                },
                actions = {
                    ProblemSetScreenActions(
                        onClickSearch = onClickSearch,
                        onClickSettings = appState::navigateToSettings,
                        onClickFilterIcon = TopAppBarManager::toggleExpandedState,
                        badgeConditionForSearch = state.searchQuery.isNotBlank(),
                        badgeConditionForFilter = state.ratingRangeValue != 800..3500
                    )
                }
            )
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.apiState == ApiState.Loading,
            onRefresh = problemSetViewModel::refreshProblemSetAndContests
        )

        Column(Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            AnimatedVisibility(
                visible = isPlatformTabRowVisible,
                modifier = Modifier.fillMaxWidth()
            ) {
                CompetracePlatformRow(
                    selectedTabIndex = state.selectedIndex,
                    platforms = problemSetViewModel.problemSetSites,
                    onClickTab = { /* TODO */ }
                )
            }
            Box(Modifier.pullRefresh(pullRefreshState)) {
                when (val apiState = state.apiState) {
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    is ApiState.Failure -> {
                        FailureScreen(
                            onClickRetry = problemSetViewModel::refreshProblemSetAndContests,
                            errorMessage = apiState.message
                        )
                    }
                    is ApiState.Success -> {
                        ProblemSetScreen(
                            state = state,
                            updateSelectedChips = problemSetViewModel::updateSelectedChips,
                            clearSelectedChips = problemSetViewModel::clearSelectedChips,
                            updateRatingRange = problemSetViewModel::updateRatingRange
                        )
                    }
                }
                CompetracePullRefreshIndicator(
                    refreshing = state.apiState == ApiState.Loading,
                    state = pullRefreshState
                )
            }
        }
    }
}