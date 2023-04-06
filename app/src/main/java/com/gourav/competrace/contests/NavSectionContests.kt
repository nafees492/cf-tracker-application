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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.NetworkFailScreen
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.ui.components.CompetracePlatformRow
import com.gourav.competrace.app_core.ui.components.CompetracePullRefreshIndicator
import com.gourav.competrace.app_core.util.*
import com.gourav.competrace.contests.presentation.ContestScreenActions
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.contests.presentation.UpcomingContestScreen

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun NavGraphBuilder.contests(
    sharedViewModel: SharedViewModel,
    contestViewModel: ContestViewModel,
    appState: CompetraceAppState
) {
    composable(route = Screens.ContestsScreen.route) {

        val context = LocalContext.current

        val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()

        val selectedIndex by contestViewModel.selectedIndex.collectAsState()
        val currentContests by contestViewModel.contests.collectAsState()
        val notificationContestIdList by contestViewModel.notificationContestIdList.collectAsState()

        val responseForKontestsContestList by contestViewModel.responseForKontestsContestList.collectAsState()
        val isRefreshing by contestViewModel.isKontestsContestListRefreshing.collectAsState()

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
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
            }
        )

        LaunchedEffect(Unit) {
            TopAppBarManager.updateTopAppBar(
                screen = Screens.ContestsScreen,
                actions = {
                    ContestScreenActions(
                        onClickSettings = appState::navigateToSettings
                    )
                }
            )
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = contestViewModel::getContestListFromKontests
        )

        Column {
            AnimatedVisibility(
                visible = isPlatformTabRowVisible,
                modifier = Modifier.fillMaxWidth()
            ) {
                CompetracePlatformRow(
                    selectedTabIndex = selectedIndex,
                    platforms = contestViewModel.contestSites,
                    onClickTab = contestViewModel::setSelectedIndexTo
                )
            }

            Box(Modifier.pullRefresh(pullRefreshState)) {
                when (responseForKontestsContestList) {
                    is ApiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    is ApiState.Failure -> {
                        NetworkFailScreen(
                            onClickRetry = contestViewModel::getContestListFromKontests,
                        )
                    }
                    is ApiState.Success -> {
                        UpcomingContestScreen(
                            contests = currentContests,
                            selectedIndex = selectedIndex,
                            onClickNotificationIcon = {
                                if (hasNotificationPermission)
                                    contestViewModel.toggleContestNotification(it)
                                else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    SnackbarManager.showMessageWithAction(
                                        messageTextId = UiText.StringResource(R.string.grant_permission_to_continue),
                                        actionLabelId = UiText.StringResource(R.string.go_to_settings),
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
                            notificationContestIdList = notificationContestIdList
                        )
                    }
                }
                CompetracePullRefreshIndicator(refreshing = isRefreshing, state = pullRefreshState)
            }
        }
    }
}
