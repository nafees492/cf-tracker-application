package com.gourav.competrace.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.ui.theme.CompetraceTheme
import com.gourav.competrace.ui.theme.DarkModePref
import com.gourav.competrace.viewmodel.MainViewModel
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
            Log.d(TAG, intent.toString())
            startActivity(intent).also {
                this.finish()
            }
        }


        setContent {
            userPreferences = UserPreferences(LocalContext.current)

            val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = CompetraceTheme.DEFAULT)
            val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)

            CompetraceTheme(currentTheme = currentTheme, darkModePref = darkModePref) {
                Application(
                    mainViewModel = mainViewModel,
                    navigateToLoginActivity = navigateToLoginActivity,
                )
            }
        }
    }

    companion object {
        const val TAG = "Main Activity"
        const val REQUEST_CODE = 3110
    }
}