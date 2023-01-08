package com.theruralguys.competrace.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.theruralguys.competrace.data.UserPreferences
import com.theruralguys.competrace.ui.theme.AppTheme
import com.theruralguys.competrace.ui.theme.CodeforcesProgressTrackerTheme
import com.theruralguys.competrace.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigateToLoginActivity: () -> Unit = {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent).also { this.finish() }
        }

        val navigateToSettingsActivity: () -> Unit = {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }



        setContent {
            userPreferences = UserPreferences(LocalContext.current.applicationContext)

            val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = AppTheme.SystemDefault.name)

            CodeforcesProgressTrackerTheme(currentTheme = currentTheme!!) {
                Application(
                    mainViewModel = mainViewModel,
                    navigateToLoginActivity = navigateToLoginActivity,
                    navigateToSettingsActivity = navigateToSettingsActivity
                )
            }
        }
    }

    companion object {
        const val TAG = "Main Activity"
    }

}