package com.gourav.competrace.progress.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarManager
import com.gourav.competrace.progress.user.presentation.*
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.app_core.ui.NetworkFailScreen
import com.gourav.competrace.progress.user.presentation.login.LoginScreen
import com.gourav.competrace.progress.user.presentation.login.LoginViewModel

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.user(
    sharedViewModel: SharedViewModel,
    userViewModel: UserViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    appState: CompetraceAppState,
    paddingValues: PaddingValues
) {
    composable(route = Screens.ProgressScreen.route) {
        val loginViewModel: LoginViewModel = hiltViewModel()

        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        val userHandle by userViewModel.userHandle.collectAsState(null)
        val responseForUserInfo by userViewModel.responseForUserInfo.collectAsState()
        val user by userViewModel.currentUser.collectAsState()

        val isRefreshing by userViewModel.isUserRefreshing.collectAsState()
        val isLoginScreenLoading by loginViewModel.isLoading.collectAsState()

        LaunchedEffect(Unit){
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ProgressScreen,
                actions = {
                    ProgressScreenActions(
                        onClickSettings = appState::navigateToSettings,
                        onClickLogOut = userViewModel::logoutUser,
                        isLogOutButtonEnabled = !userHandle.isNullOrEmpty()
                    )
                }
            )
        }

        val swipeRefreshStateProgress = rememberSwipeRefreshState(isRefreshing = isRefreshing)

        val swipeRefreshStateLogin = rememberSwipeRefreshState(isRefreshing = isLoginScreenLoading)

        var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

        Column(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            AnimatedVisibility(visible = isPlatformTabRowVisible, modifier = Modifier.fillMaxWidth()) {
                CompetracePlatformRow(
                    selectedTabIndex = selectedTabIndex,
                    platforms = userViewModel.userSites,
                    onClickTab = { selectedTabIndex = it }
                )
            }
            userHandle?.let { handle ->
                Crossfade(
                    targetState = handle.isEmpty()
                ) {
                    if (it) {
                        SwipeRefresh(
                            state = swipeRefreshStateLogin,
                            onRefresh = { /*TODO*/ },
                            indicator = CompetraceSwipeRefreshIndicator,
                            swipeEnabled = false,
                        ) {
                            LoginScreen(loginViewModel = loginViewModel)
                        }
                    } else {
                        SwipeRefresh(
                            state = swipeRefreshStateProgress,
                            onRefresh = userViewModel::refreshUserInfo,
                            indicator = CompetraceSwipeRefreshIndicator,
                        ) {
                            when (responseForUserInfo) {
                                is ApiState.Loading -> {
                                    Box(modifier = Modifier.fillMaxSize())
                                }
                                is ApiState.Failure -> {
                                    NetworkFailScreen(
                                        onClickRetry = userViewModel::refreshUserInfo
                                    )
                                }
                                is ApiState.Success -> {
                                    user?.let { you ->
                                        ProgressScreen(
                                            user = you,
                                            goToSubmission = appState::navigateToUserSubmissionScreen,
                                            goToParticipatedContests = appState::navigateToParticipatedContestsScreen,
                                            userSubmissionsViewModel = userSubmissionsViewModel,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}