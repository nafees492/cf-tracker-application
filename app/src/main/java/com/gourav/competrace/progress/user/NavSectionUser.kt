package com.gourav.competrace.progress.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarManager
import com.gourav.competrace.progress.user.presentation.*
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.app_core.ui.FailureScreen
import com.gourav.competrace.app_core.ui.components.CompetracePullRefreshIndicator
import com.gourav.competrace.progress.user.presentation.login.LoginScreen
import com.gourav.competrace.progress.user.presentation.login.LoginViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.user(
    userViewModel: UserViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    appState: CompetraceAppState,
    paddingValues: PaddingValues
) {
    composable(route = Screens.ProgressScreen.route) {
        val loginViewModel: LoginViewModel = hiltViewModel()

        val isPlatformTabRowVisible by appState.isPlatformsTabRowVisible.collectAsState()

        val userHandle by userViewModel.userHandle.collectAsState(null)
        val responseForUserInfo by userViewModel.responseForUserInfo.collectAsState()
        val user by userViewModel.currentUser.collectAsState()

        val isUserScreenLoading by userViewModel.isUserRefreshing.collectAsState()
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

        val pullRefreshStateUser = rememberPullRefreshState(
            refreshing = isUserScreenLoading,
            onRefresh = userViewModel::refreshUserInfo
        )

        val pullRefreshStateLogin = rememberPullRefreshState(
            refreshing = isLoginScreenLoading,
            onRefresh = {  }
        )

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
                        Box(Modifier.pullRefresh(pullRefreshStateLogin, enabled = false)) {
                            LoginScreen(loginViewModel = loginViewModel)
                            CompetracePullRefreshIndicator(
                                refreshing = isLoginScreenLoading,
                                state = pullRefreshStateLogin
                            )
                        }
                    } else {
                        Box(Modifier.pullRefresh(pullRefreshStateUser)) {
                            when (val apiState = responseForUserInfo) {
                                is ApiState.Loading -> {
                                    Box(modifier = Modifier.fillMaxSize())
                                }
                                is ApiState.Failure -> {
                                    FailureScreen(
                                        onClickRetry = userViewModel::refreshUserInfo,
                                        errorMessage = apiState.message
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
                            CompetracePullRefreshIndicator(
                                refreshing = isUserScreenLoading,
                                state = pullRefreshStateUser
                            )
                        }
                    }
                }
            }
        }
    }
}