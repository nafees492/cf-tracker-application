package com.gourav.competrace.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.gourav.competrace.ui.components.BottomNavigationBar
import com.gourav.competrace.ui.components.CompetraceTopAppBar
import com.gourav.competrace.ui.controllers.ToolbarStyles
import com.gourav.competrace.ui.controllers.TopAppBarController
import com.gourav.competrace.ui.navigation.NavigationHost
import com.gourav.competrace.ui.navigation.Screens
import com.gourav.competrace.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun Application(
    mainViewModel: MainViewModel,
    navigateToLoginActivity: () -> Unit,
) {
    val navController = rememberNavController()

    val topAppBarScrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    val topAppBarController = remember {
        object : TopAppBarController {
            override var title: String by mutableStateOf(Screens.ContestsScreen.title)

            override var scrollBehavior: TopAppBarScrollBehavior by mutableStateOf(
                topAppBarScrollBehaviour
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
        CompetraceTopAppBar(
            topAppBarController = topAppBarController
        )
    }

    val bottomNavBar: @Composable () -> Unit = {
        BottomNavigationBar(
            navController = navController,
            currentScreen = topAppBarController.title
        )
    }

    Surface {
        Scaffold(
            topBar = topBar,
            bottomBar = bottomNavBar,
        ) {
            NavigationHost(
                topAppBarController = topAppBarController,
                navController = navController,
                mainViewModel = mainViewModel,
                paddingValues = it,
                navigateToLoginActivity = navigateToLoginActivity,
            )
        }
    }

}
