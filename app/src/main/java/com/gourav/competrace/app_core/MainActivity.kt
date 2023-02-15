package com.gourav.competrace.app_core

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.ui.theme.CompetraceTheme
import com.gourav.competrace.ui.theme.DarkModePref
import com.gourav.competrace.app_core.presentation.Application
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val applicationViewModel: ApplicationViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            userPreferences = UserPreferences(LocalContext.current)

            val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = CompetraceTheme.DEFAULT)
            val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)

            CompetraceTheme(currentTheme = currentTheme, darkModePref = darkModePref) {
                Application(mainViewModel = mainViewModel, applicationViewModel = applicationViewModel)
            }
        }
    }

    companion object {
        const val TAG = "Main Activity"
    }
}