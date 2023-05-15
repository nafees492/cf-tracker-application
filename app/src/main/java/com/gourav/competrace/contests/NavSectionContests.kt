package com.gourav.competrace.contests

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gourav.competrace.R
import com.gourav.competrace.app_core.TrackScreen
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.FailureScreen
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetracePullRefreshIndicator
import com.gourav.competrace.app_core.util.*
import com.gourav.competrace.contests.presentation.ContestScreenActions
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.contests.presentation.UpcomingContestScreen

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.contests(
    contestViewModel: ContestViewModel,
    appState: CompetraceAppState,
    paddingValues: PaddingValues
) {
    composable(route = Screens.ContestsScreen.route) {
        TrackScreen(screen = Screens.ContestsScreen)

        val context = LocalContext.current

        val isPlatformTabRowVisible by appState.isPlatformsTabRowVisible.collectAsStateWithLifecycle()
        val screenState by contestViewModel.screenState.collectAsStateWithLifecycle()

        var hasNotificationPermission by remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                )
            } else mutableStateOf(true)
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted -> hasNotificationPermission = isGranted }
        )

        LaunchedEffect(Unit) {
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ContestsScreen,
                actions = {
                    ContestScreenActions(
                        openSettings = appState::navigateToSettings,
                        openSite = {
                            val contestsUrl =  contestViewModel
                                .contestSites[screenState.selectedIndex].contestsUrl
                            context.loadUrl(contestsUrl)
                        }
                    )
                }
            )
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = screenState.apiState == ApiState.Loading,
            onRefresh = contestViewModel::getContestListFromKontests
        )

        Column(Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            AnimatedVisibility(
                visible = isPlatformTabRowVisible,
                modifier = Modifier.fillMaxWidth()
            ) {
                CompetracePlatformRow(
                    selectedTabIndex = screenState.selectedIndex,
                    platforms = contestViewModel.contestSites,
                    onClickTab = contestViewModel::setSelectedIndexTo
                )
            }

            Box(Modifier.pullRefresh(pullRefreshState)) {
                when (val apiState = screenState.apiState) {
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    is ApiState.Failure -> {
                        FailureScreen(
                            onClickRetry = contestViewModel::getContestListFromKontests,
                            errorMessage = apiState.message
                        )
                    }
                    is ApiState.Success -> {
                        UpcomingContestScreen(
                            state = screenState,
                            onClickNotificationIcon = {
                                if (hasNotificationPermission)
                                    contestViewModel.toggleContestNotification(it)
                                else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    SnackbarManager.showMessageWithAction(
                                        message = UiText.StringResource(R.string.grant_permission_to_continue),
                                        actionLabel = UiText.StringResource(R.string.go_to_settings),
                                        action = {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                    putExtra(
                                                        Settings.EXTRA_APP_PACKAGE,
                                                        context.packageName
                                                    )
                                                }.also(context::startActivity)
                                            }
                                        }
                                    )
                                }
                            },
                        )
                    }
                }
                CompetracePullRefreshIndicator(
                    refreshing = screenState.apiState == ApiState.Loading,
                    state = pullRefreshState
                )
            }
        }
    }
}
