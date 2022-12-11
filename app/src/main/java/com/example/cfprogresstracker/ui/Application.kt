package com.example.cfprogresstracker.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.cfprogresstracker.ui.components.BottomNavigationBar
import com.example.cfprogresstracker.ui.components.Toolbar
import com.example.cfprogresstracker.ui.controllers.ToolbarController
import com.example.cfprogresstracker.ui.controllers.ToolbarStyles
import com.example.cfprogresstracker.ui.navigation.NavigationHost
import com.example.cfprogresstracker.ui.navigation.Screens
import com.example.cfprogresstracker.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application(
    mainViewModel: MainViewModel,
    navigateToLoginActivity: () -> Unit,
    navigateToSettingsActivity: () -> Unit,
) {
    val navController = rememberNavController()

    val topAppBarDefault = TopAppBarDefaults.pinnedScrollBehavior()

    val toolbarController = remember {
        object : ToolbarController {
            override var title: String by mutableStateOf(Screens.ContestsScreen.title)

            override var scrollBehavior: TopAppBarScrollBehavior by mutableStateOf(
                topAppBarDefault
            )

            override var toolbarStyle: ToolbarStyles by mutableStateOf(ToolbarStyles.Small)

            override var onClickNavUp: () -> Unit by mutableStateOf({})

            override var expandToolbar: Boolean by mutableStateOf(false)

            override var expandedContent: @Composable () -> Unit by mutableStateOf({})

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

    val bottomNavBar: @Composable () -> Unit = {
        BottomNavigationBar(
            navController = navController,
            currentScreen = toolbarController.title
        )
    }

    Surface() {
        Scaffold(
            topBar = topBar,
            bottomBar = bottomNavBar,
        ) {
            NavigationHost(
                toolbarController = toolbarController,
                navController = navController,
                mainViewModel = mainViewModel,
                paddingValues = it,
                navigateToLoginActivity = navigateToLoginActivity,
                navigateToSettingsActivity = navigateToSettingsActivity
            )
        }
    }

}
