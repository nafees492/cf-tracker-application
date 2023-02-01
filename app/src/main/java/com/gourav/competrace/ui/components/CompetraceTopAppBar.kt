package com.gourav.competrace.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import com.gourav.competrace.R
import com.gourav.competrace.ui.controllers.TopAppBarController
import com.gourav.competrace.ui.controllers.TopAppBarStyles
import com.gourav.competrace.ui.navigation.Screens

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterial3Api
@Composable
fun CompetraceTopAppBar(
    topAppBarController: TopAppBarController
) {
    val homeScreens = listOf(
        Screens.ContestsScreen.title,
        Screens.ProblemSetScreen.title,
        Screens.ProgressScreen.title
    )

    val navigationIcon: @Composable (() -> Unit) = {
        AnimatedVisibility(visible = !homeScreens.contains(topAppBarController.title)) {
            NormalIconButton(
                iconId = R.drawable.ic_arrow_back_24px,
                onClick = topAppBarController.onClickNavUp,
                contentDescription = "Back Button"
            )
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
                                text = topAppBarController.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.animateContentSize()
                            )
                        },
                        actions = topAppBarController.actions,
                        scrollBehavior = topAppBarController.scrollBehavior,
                    )
                is TopAppBarStyles.Medium ->
                    MediumTopAppBar(
                        navigationIcon = navigationIcon,
                        title = {
                            Text(
                                text = topAppBarController.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = topAppBarController.actions,
                        scrollBehavior = topAppBarController.scrollBehavior
                    )
                is TopAppBarStyles.Large ->
                    LargeTopAppBar(
                        navigationIcon = navigationIcon,
                        title = {
                            Text(
                                text = topAppBarController.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = topAppBarController.actions,
                        scrollBehavior = topAppBarController.scrollBehavior
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