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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import com.gourav.competrace.R
import com.gourav.competrace.app_core.presentation.SharedViewModel
import com.gourav.competrace.app_core.util.Screens
import com.gourav.competrace.app_core.util.TopAppBarStyles

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun CompetraceTopAppBar(
    sharedViewModel: SharedViewModel,
) {
    val topAppBarController = sharedViewModel.topAppBarController
    val isPlatformTabRowVisible by sharedViewModel.isPlatformsTabRowVisible.collectAsState()
    val scope = rememberCoroutineScope()

    val homeScreens = listOf(
        Screens.ContestsScreen.title,
        Screens.ProblemSetScreen.title,
        Screens.ProgressScreen.title
    )

    val navigationIcon: @Composable (() -> Unit) = {
        AnimatedContent(targetState = homeScreens.contains(topAppBarController.screenTitle)) {
            if (it){
                IconButton(onClick = { sharedViewModel.setIsPlatformsTabRowVisible(!isPlatformTabRowVisible) }) {
                    ExpandArrow(expanded = isPlatformTabRowVisible)
                }
            } else {
                CompetraceIconButton(
                    iconId = R.drawable.ic_arrow_back_24px,
                    onClick = topAppBarController.onClickNavUp,
                    contentDescription = "Back Button"
                )
            }
        }
    }

    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Box {
            when (topAppBarController.topAppBarStyle) {
                is TopAppBarStyles.Small ->
                    TopAppBar(
                        navigationIcon = navigationIcon,
                        title = {
                            Text(
                                text = topAppBarController.screenTitle,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.animateContentSize()
                            )
                        },
                        actions = topAppBarController.actions,
                    )
                is TopAppBarStyles.Medium ->
                    MediumTopAppBar(
                        navigationIcon = navigationIcon,
                        title = {
                            Text(
                                text = topAppBarController.screenTitle,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = topAppBarController.actions,
                    )
                is TopAppBarStyles.Large ->
                    LargeTopAppBar(
                        navigationIcon = navigationIcon,
                        title = {
                            Text(
                                text = topAppBarController.screenTitle,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = topAppBarController.actions,
                    )
            }
            this@Column.AnimatedVisibility(
                visible = topAppBarController.isSearchWidgetOpen,
                enter = scaleIn(
                    animationSpec = tween(easing = FastOutSlowInEasing),
                    transformOrigin = TransformOrigin(0.8f, 0.5f)
                ) + fadeIn(animationSpec = tween(easing = FastOutSlowInEasing)),
                exit = scaleOut(
                    animationSpec = tween(easing = FastOutSlowInEasing),
                    transformOrigin = TransformOrigin(0.8f, 0.5f)
                ) + fadeOut(animationSpec = tween(easing = FastOutSlowInEasing))
            ) {
                topAppBarController.searchWidgetContent()
            }
        }

        AnimatedVisibility(
            visible = topAppBarController.isTopAppBarExpanded && !topAppBarController.isSearchWidgetOpen,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            topAppBarController.expandedTopAppBarContent()
        }
    }
}