package com.example.cfprogresstracker.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.cfprogresstracker.ui.components.BottomNavigationBar
import com.example.cfprogresstracker.ui.components.Toolbar
import com.example.cfprogresstracker.ui.controllers.SearchWidgetState
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.controllers.ToolbarStyles
import com.example.cfprogresstracker.ui.navigation.NavigationHost
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.ui.theme.CodeforcesProgressTrackerTheme
import com.example.cfprogresstracker.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application(
    navigateToLoginActivity: () -> Unit,
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()

    val toolbarController = remember {
        object : ToolbarController {
            override var title: String by mutableStateOf(Screens.ContestsScreen.title)

            override var scrollBehavior: TopAppBarScrollBehavior by mutableStateOf(
                TopAppBarDefaults.pinnedScrollBehavior()
            )

            override var toolbarStyle: ToolbarStyles by mutableStateOf(ToolbarStyles.Small)

            override var onClickNavUp: () -> Unit by mutableStateOf({})

            override var searchWidgetState: SearchWidgetState by mutableStateOf(
                SearchWidgetState.Closed()
            )

            override var actions: @Composable RowScope.() -> Unit by mutableStateOf({})

            override fun clearActions() {
                actions = {}
            }
        }
    }

    val topBar: @Composable () -> Unit = {
        Toolbar(
            toolbarController = toolbarController
        )
    }

    val bottomBarHeight = 75.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    var bottomBarOffsetHeightPx by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = bottomBarOffsetHeightPx + available.y
                bottomBarOffsetHeightPx = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    val bottomNavBar: @Composable () -> Unit = {
        BottomNavigationBar(
            navController = navController,
            currentScreen = toolbarController.title,
            bottomBarHeight = bottomBarHeight,
            bottomBarOffsetHeightPx = bottomBarOffsetHeightPx
        )
    }

    CodeforcesProgressTrackerTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                topBar = topBar,
                bottomBar = bottomNavBar,
                modifier = Modifier.nestedScroll(nestedScrollConnection)
            ) {
                NavigationHost(
                    toolbarController = toolbarController,
                    navController = navController,
                    navigateToLoginActivity = navigateToLoginActivity,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}
