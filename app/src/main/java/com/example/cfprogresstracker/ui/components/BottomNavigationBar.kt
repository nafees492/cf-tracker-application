package com.example.cfprogresstracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.ui.navigation.Screens
import kotlin.math.roundToInt

sealed class BottomNavigationItem(val screen: Screens, val icon: Int) {
    object Contest :
        BottomNavigationItem(Screens.ContestsScreen, R.drawable.ic_round_leaderboard_24)

    object ProblemSet :
        BottomNavigationItem(Screens.ProblemSetScreen, R.drawable.ic_round_assignment_24)

    object Progress :
        BottomNavigationItem(Screens.ProgressScreen, R.drawable.ic_round_insights_24)
}

@SuppressLint("RememberReturnType")
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentScreen: String,
    bottomBarHeight: Dp,
    bottomBarOffsetHeightPx: Float,
) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = remember {
        listOf(
            BottomNavigationItem.Contest,
            BottomNavigationItem.ProblemSet,
            BottomNavigationItem.Progress
        )
    }
    val sections = listOf(
        Screens.ContestsScreen.title,
        Screens.ProblemSetScreen.title,
        Screens.ProgressScreen.title
    )

    var visibility by remember { mutableStateOf(true) }
    visibility = sections.contains(currentScreen)

    AnimatedVisibility(
        visible = visibility,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        NavigationBar(
            modifier = Modifier
                .height(bottomBarHeight)
                .offset { IntOffset(x = 0, y = -bottomBarOffsetHeightPx.roundToInt()) }
                .graphicsLayer {
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    clip = true
                },
            tonalElevation = 24.dp
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(text = item.screen.title) },
                    selected = currentScreen == item.screen.title,
                    onClick = {
                        selectedItem = index
                        navController.navigate(item.screen.name) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // re-selecting the same item
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}