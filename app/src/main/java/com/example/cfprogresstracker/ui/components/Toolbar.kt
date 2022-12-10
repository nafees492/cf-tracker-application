package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.controllers.ToolbarStyles
import com.example.cfprogresstracker.ui.navigation.Screens

@ExperimentalMaterial3Api
@Composable
fun Toolbar(
    toolbarController: ToolbarController
) {
    val homeScreens = listOf(
        Screens.ContestsScreen.title,
        Screens.ProblemSetScreen.title,
        Screens.ProgressScreen.title
    )
    val navigationIcon: @Composable (() -> Unit) = {
        AnimatedVisibility(visible = !homeScreens.contains(toolbarController.title)) {
            IconButton(onClick = toolbarController.onClickNavUp) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back Button"
                )
            }
        }
    }

    Column(
        modifier = Modifier.animateContentSize()
    ) {
        when (toolbarController.toolbarStyle) {
            is ToolbarStyles.Small ->
                TopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = toolbarController.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.animateContentSize()
                        )
                    },
                    actions = toolbarController.actions,
                    scrollBehavior = toolbarController.scrollBehavior,
                )
            is ToolbarStyles.Medium ->
                MediumTopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = toolbarController.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = toolbarController.actions,
                    scrollBehavior = toolbarController.scrollBehavior
                )
            is ToolbarStyles.Large ->
                LargeTopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = toolbarController.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = toolbarController.actions,
                    scrollBehavior = toolbarController.scrollBehavior
                )
        }

        AnimatedVisibility(
            visible = toolbarController.expandToolbar,
            enter = fadeIn(),
            exit = fadeOut(
                animationSpec = tween(100)
            ),
        ) {
            toolbarController.expandedContent()
        }
    }
}