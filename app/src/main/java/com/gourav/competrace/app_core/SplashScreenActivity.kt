package com.gourav.competrace.app_core

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        setContent {
            val userPreferences = UserPreferences(LocalContext.current)
            val userHandle by userPreferences.handleNameFlow.collectAsState(initial = "")

            splashScreen.setKeepOnScreenCondition{ true }
            
            LaunchedEffect(Unit){
                delay(100)
                val intent = if (userHandle.isNullOrBlank())
                    Intent(this@SplashScreenActivity, LoginActivity::class.java)
                else
                    Intent(this@SplashScreenActivity, MainActivity::class.java)

                Log.d(TAG, intent.toString())
                startActivity(intent).also {
                    this@SplashScreenActivity.finish()
                    Log.d(TAG,  "Finished")
                }
            }
        }
    }

    companion object {
        const val TAG = "SplashScreenActivity"
    }
}