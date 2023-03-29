package com.gourav.competrace.progress.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetraceSwipeRefreshIndicator
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarManager
import com.gourav.competrace.progress.user.presentation.*
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.settings.SettingsAlertDialog
import com.gourav.competrace.app_core.ui.NetworkFailScreen

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.user(
    sharedViewModel: SharedViewModel,
    userViewModel: UserViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    navController: NavController
) {
    composable(route = Screens.ProgressScreen.route) {
        val loginViewModel: LoginViewModel = hiltViewModel()

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()
        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        val userHandle by userViewModel.userHandle.collectAsState(null)
        val responseForUserInfo by userViewModel.responseForUserInfo.collectAsState()
        val isRefreshing by userViewModel.isUserRefreshing.collectAsState()
        val user by userViewModel.currentUser.collectAsState()

        LaunchedEffect(Unit){
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ProgressScreen,
                actions = {
                    ProgressScreenActions(
                        onClickSettings = sharedViewModel::openSettingsDialog,
                        onClickLogOut = userViewModel::logoutUser,
                        isLogOutButtonEnabled = !userHandle.isNullOrEmpty()
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
            userHandle?.let { handle ->
                Crossfade(
                    targetState = handle.isEmpty()
                ) {
                    if (it) {
                        LoginScreen(loginViewModel = loginViewModel)
                    } else {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = userViewModel::refreshUserInfo,
                            indicator = CompetraceSwipeRefreshIndicator
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
                                    ProgressScreen(
                                        user = user,
                                        goToSubmission = {
                                            navController.navigate(Screens.UserSubmissionsScreen.route)
                                        },
                                        goToParticipatedContests = {
                                            navController.navigate(Screens.ParticipatedContestsScreen.route)
                                        },
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