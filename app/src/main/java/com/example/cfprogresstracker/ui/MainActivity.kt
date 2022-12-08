package com.example.cfprogresstracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.cfprogresstracker.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigateToLoginActivity: () -> Unit = {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent).also { this.finish() }
        }



        setContent {
            Application(
                mainViewModel = mainViewModel,
                navigateToLoginActivity = navigateToLoginActivity,
            )
        }
    }

    companion object {
        const val TAG = "Main Activity"
    }

}