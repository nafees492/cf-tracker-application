package com.theruralguys.competrace.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.theruralguys.competrace.data.UserPreferences
import com.theruralguys.competrace.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        setContent {
            val coroutineScope = rememberCoroutineScope()
            userPreferences = UserPreferences(LocalContext.current)
            val userHandle by userPreferences.handleNameFlow.collectAsState(initial = "")

            val isSplashScreenLoading by mainViewModel.isSplashScreenLoading.collectAsState(initial = true)
            splashScreen.setKeepOnScreenCondition{ isSplashScreenLoading }
            
            LaunchedEffect(Unit){
                delay(100)
                val intent = if (userHandle.isNullOrBlank())
                    Intent(this@SplashScreenActivity, LoginActivity::class.java)
                else
                    Intent(this@SplashScreenActivity, MainActivity::class.java)
                intent.apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
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