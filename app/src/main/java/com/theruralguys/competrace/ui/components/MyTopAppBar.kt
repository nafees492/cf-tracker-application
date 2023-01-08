package com.theruralguys.competrace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.theruralguys.competrace.ui.controllers.ToolbarStyles
import com.theruralguys.competrace.ui.controllers.TopAppBarController
import com.theruralguys.competrace.ui.navigation.Screens

@ExperimentalMaterial3Api
@Composable
fun MyTopAppBar(
    topAppBarController: TopAppBarController
) {
    val homeScreens = listOf(
        Screens.ContestsScreen.title,
        Screens.ProblemSetScreen.title,
        Screens.ProgressScreen.title
    )

    val navigationIcon: @Composable (() -> Unit) = {
        AnimatedVisibility(visible = !homeScreens.contains(topAppBarController.title)) {
            IconButton(onClick = topAppBarController.onClickNavUp) {
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
        when (topAppBarController.toolbarStyle) {
            is ToolbarStyles.Small ->
                TopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = topAppBarController.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.animateContentSize()
                        )
                    },
                    actions = topAppBarController.actions,
                    scrollBehavior = topAppBarController.scrollBehavior,
                )
            is ToolbarStyles.Medium ->
                MediumTopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = topAppBarController.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = topAppBarController.actions,
                    scrollBehavior = topAppBarController.scrollBehavior
                )
            is ToolbarStyles.Large ->
                LargeTopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = topAppBarController.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = topAppBarController.actions,
                    scrollBehavior = topAppBarController.scrollBehavior
                )
        }

        AnimatedVisibility(
            visible = topAppBarController.expandToolbar,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            topAppBarController.expandedContent()
        }
    }
}