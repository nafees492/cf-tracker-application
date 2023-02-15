package com.gourav.competrace.login

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.ui.screens.LoginScreen
import com.gourav.competrace.ui.theme.CompetraceTheme
import com.gourav.competrace.ui.theme.DarkModePref
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val userPreferences = UserPreferences(LocalContext.current)

            val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = CompetraceTheme.DEFAULT)
            val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)

            CompetraceTheme(currentTheme = currentTheme, darkModePref = darkModePref) {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(loginViewModel = loginViewModel)
                }
            }
        }
    }

    companion object {
        const val TAG = "Login Activity"
    }
}