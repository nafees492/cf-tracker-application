package com.gourav.competrace

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gourav.competrace.app_core.CompetraceInAppUpdate
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.ui.theme.CompetraceThemeNames
import com.gourav.competrace.app_core.ui.theme.DarkModePref
import com.gourav.competrace.app_core.ui.theme.CompetraceTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        setContent {
            val isSplashScreenOn by sharedViewModel.isSplashScreenOn.collectAsState()
            splashScreen.setKeepOnScreenCondition { isSplashScreenOn }

            val competraceInAppUpdate = CompetraceInAppUpdate(applicationContext)
            competraceInAppUpdate.checkForAppUpdates()

            val userPreferences = UserPreferences(LocalContext.current)

            val currentTheme by userPreferences.currentThemeFlow.collectAsStateWithLifecycle(
                CompetraceThemeNames.DEFAULT
            )
            val darkModePref by userPreferences.darkModePrefFlow.collectAsStateWithLifecycle(
                DarkModePref.SYSTEM_DEFAULT
            )

            CompetraceTheme(currentTheme = currentTheme, darkModePref = darkModePref) {
                Application()
            }
        }
    }

    companion object {
        private const val TAG = "Main Activity"
    }
}