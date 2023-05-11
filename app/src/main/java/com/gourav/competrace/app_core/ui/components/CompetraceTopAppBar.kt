package com.gourav.competrace.app_core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.util.TopAppBarManager

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun CompetraceTopAppBar(
    appState: CompetraceAppState
) {
    val topAppBarValues by TopAppBarManager.topAppBarValues.collectAsStateWithLifecycle()
    val isPlatformTabRowVisible by appState.isPlatformsTabRowVisible.collectAsStateWithLifecycle()
    val isConnected by appState.isConnectedToNetwork.collectAsStateWithLifecycle()

    val navigationIcon: @Composable (() -> Unit) = {
        AnimatedContent(targetState = appState.shouldShowBottomBar) {
            if (it) {
                IconButton(onClick = { appState.toggleIsPlatformsTabRowVisibleTo(!isPlatformTabRowVisible) }) {
                    ExpandArrow(expanded = isPlatformTabRowVisible)
                }
            } else {
                CompetraceIconButton(
                    iconId = R.drawable.ic_arrow_back_24px,
                    onClick = appState::upPress,
                    contentDescription = stringResource(R.string.cd_go_back)
                )
            }
        }
    }

    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Box {
            TopAppBar(
                navigationIcon = navigationIcon,
                title = {
                    Text(
                        text = stringResource(id = topAppBarValues.currentScreen.titleId),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.animateContentSize(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = topAppBarValues.actions,
            )
            this@Column.AnimatedVisibility(
                visible = topAppBarValues.isSearchWidgetOpen,
                enter = scaleIn(
                    animationSpec = tween(easing = FastOutSlowInEasing),
                    transformOrigin = TransformOrigin(0.9f, 0.5f)
                ) + fadeIn(animationSpec = tween(easing = FastOutSlowInEasing)),
                exit = scaleOut(
                    animationSpec = tween(easing = FastOutSlowInEasing),
                    transformOrigin = TransformOrigin(0.9f, 0.5f)
                ) + fadeOut(animationSpec = tween(easing = FastOutSlowInEasing))
            ) {
                topAppBarValues.searchWidget()
            }
        }

        AnimatedVisibility(
            visible = topAppBarValues.isTopAppBarExpanded && !topAppBarValues.isSearchWidgetOpen,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            topAppBarValues.expandedTopAppBarContent()
        }

        ConnectivityStatus(isConnected = isConnected)
    }
}