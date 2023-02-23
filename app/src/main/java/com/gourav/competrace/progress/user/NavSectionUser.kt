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
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.presentation.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.progress.user.presentation.*
import com.gourav.competrace.progress.user_submissions.presentation.UserSubmissionsViewModel
import com.gourav.competrace.ui.components.SettingsAlertDialog
import com.gourav.competrace.ui.screens.NetworkFailScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.user(
    sharedViewModel: SharedViewModel,
    userViewModel: UserViewModel,
    userSubmissionsViewModel: UserSubmissionsViewModel,
    userPreferences: UserPreferences,
    navController: NavController
) {
    val topAppBarController = sharedViewModel.topAppBarController

    composable(route = Screens.ProgressScreen.name) {
        val loginViewModel: LoginViewModel = hiltViewModel()

        topAppBarController.apply {
            screenTitle = Screens.ProgressScreen.title
            isTopAppBarExpanded = false
            isSearchWidgetOpen = false
        }

        val scope = rememberCoroutineScope()

        val userHandle by userPreferences.handleNameFlow.collectAsState(null)

        val isSettingsDialogueOpen by sharedViewModel.isSettingsDialogueOpen.collectAsState()

        SettingsAlertDialog(
            openSettingsDialog = isSettingsDialogueOpen,
            dismissSettingsDialogue = sharedViewModel::dismissSettingsDialog,
            userPreferences = userPreferences
        )

        val onClickLogoutBtn: () -> Unit = {
            loginViewModel.responseForCheckUsernameAvailable = ApiState.Empty
            scope.launch {
                userPreferences.setHandleName("")
            }
        }

        topAppBarController.actions = {
            ProgressScreenActions(
                onClickSettings = sharedViewModel::openSettingsDialog,
                onClickLogOut = onClickLogoutBtn,
                isLogOutButtonEnabled = !userHandle.isNullOrEmpty()
            )
        }

        val isRefreshing by userViewModel.isUserRefreshing.collectAsState()
        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

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
            userHandle?.let { handle ->
                Crossfade(
                    targetState = handle.isEmpty()
                ) {
                    if (it) {
                        LoginScreen(loginViewModel = loginViewModel)
                    } else {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = {
                                userViewModel.requestForUserInfo(
                                    userPreferences = userPreferences,
                                    isForced = true
                                )
                            }
                        ) {
                            when (userViewModel.responseForUserInfo) {
                                is ApiState.Empty -> {
                                    userViewModel.requestForUserInfo(
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
                                            userViewModel.requestForUserInfo(
                                                userPreferences = userPreferences,
                                                isForced = true
                                            )
                                        }
                                    )
                                }
                                else /* ApiState.Success */ -> {
                                    val user by userViewModel.currentUser.collectAsState()

                                    ProgressScreen(
                                        user = user,
                                        goToSubmission = {
                                            navController.navigate(Screens.UserSubmissionsScreen.name)
                                        },
                                        goToParticipatedContests = {
                                            navController.navigate(Screens.ParticipatedContestsScreen.name)
                                        },
                                        userSubmissionsViewModel = userSubmissionsViewModel,
                                        userPreferences = userPreferences
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