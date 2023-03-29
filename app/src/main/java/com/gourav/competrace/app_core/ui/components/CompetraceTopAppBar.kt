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
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.CompetraceAppState
import com.gourav.competrace.app_core.ui.SharedViewModel
import com.gourav.competrace.app_core.util.TopAppBarManager

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun CompetraceTopAppBar(
    sharedViewModel: SharedViewModel,
    appState: CompetraceAppState
) {
    val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()
    val topAppBarValues by TopAppBarManager.topAppBarValues.collectAsState()

    val navigationIcon: @Composable (() -> Unit) =  {
        AnimatedContent(targetState = appState.shouldShowBottomBar) {
            if (it){
                IconButton(onClick = { sharedViewModel.toggleIsPlatformsTabRowVisibleTo(!isPlatformTabRowVisible) }) {
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
                        modifier = Modifier.animateContentSize()
                    )
                },
                actions = topAppBarValues.actions,
            )
            this@Column.AnimatedVisibility(
                visible = topAppBarValues.isSearchWidgetOpen,
                enter = scaleIn(
                    animationSpec = tween(easing = FastOutSlowInEasing),
                    transformOrigin = TransformOrigin(0.8f, 0.5f)
                ) + fadeIn(animationSpec = tween(easing = FastOutSlowInEasing)),
                exit = scaleOut(
                    animationSpec = tween(easing = FastOutSlowInEasing),
                    transformOrigin = TransformOrigin(0.8f, 0.5f)
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
    }
}