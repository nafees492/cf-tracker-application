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
import androidx.compose.ui.platform.LocalContext
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
import com.gourav.competrace.app_core.ui.components.SearchAppBar
import com.gourav.competrace.app_core.util.loadUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.problemSet(
    problemSetViewModel: ProblemSetViewModel,
    appState: CompetraceAppState,
    paddingValues: PaddingValues
) {
    composable(route = Screens.ProblemSetScreen.route) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        val isPlatformTabRowVisible by appState.isPlatformsTabRowVisible.collectAsStateWithLifecycle()
        val state by problemSetViewModel.screenState.collectAsStateWithLifecycle()
        val searchQuery by problemSetViewModel.searchQuery.collectAsStateWithLifecycle()

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
                        openSettings = appState::navigateToSettings,
                        openSite = {
                            val problemSetUrl =
                                problemSetViewModel.problemSetSites[state.selectedIndex].problemSetUrl
                            context.loadUrl(problemSetUrl)
                        },
                        badgeConditionForSearch = searchQuery.isNotBlank(),
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
                            updateSelectedTags = problemSetViewModel::updateSelectedTags,
                            clearSelectedTags = problemSetViewModel::clearSelectedTags,
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