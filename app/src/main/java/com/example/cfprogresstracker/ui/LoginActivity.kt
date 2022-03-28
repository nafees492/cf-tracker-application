package com.example.cfprogresstracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.retrofit.util.ApiState
import com.example.cfprogresstracker.ui.components.CircularIndeterminateProgressBar
import com.example.cfprogresstracker.ui.theme.CodeforcesProgressTrackerTheme
import com.example.cfprogresstracker.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences
    private lateinit var coroutineScope: CoroutineScope

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            coroutineScope = rememberCoroutineScope()
            userPreferences = UserPreferences(LocalContext.current.applicationContext)

            CodeforcesProgressTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d(TAG, "Content Set")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoginScreen()
                    }
                }
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun LoginScreen() {
        val userHandle by userPreferences.handleNameFlow.collectAsState(initial = "")

        var inputHandle by remember { mutableStateOf("") }
        val onInputChange: (String) -> Unit = {
            inputHandle = it
        }

        if (userHandle.isNullOrBlank()) {
            when (val apiResult = mainViewModel.responseForUserInfo) {
                is ApiState.Empty -> {
                    InputHandle(inputHandle = inputHandle, onValueChange = onInputChange)
                }
                is ApiState.Loading -> {
                    CircularIndeterminateProgressBar(isDisplayed = true)
                }
                is ApiState.Success<*> -> {
                    if (apiResult.response.status == "OK") {
                        coroutineScope.launch(Dispatchers.IO) {
                            userPreferences.setHandleName(inputHandle)
                        }
                    } else {
                        Toast.makeText(this, apiResult.response.comment, Toast.LENGTH_SHORT).show()
                        mainViewModel.responseForUserInfo = ApiState.Empty
                    }
                }
                is ApiState.Failure -> {
                    Toast.makeText(this, apiResult.msg.toString(), Toast.LENGTH_SHORT).show()
                    mainViewModel.responseForUserInfo = ApiState.Empty
                }
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent).also { this.finish() }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun InputHandle(
        inputHandle: String,
        onValueChange: (String) -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column {
            OutlinedTextField(
                value = inputHandle,
                onValueChange = onValueChange,
                label = { Text("Enter Codeforces Handle") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboardController?.hide()
                        mainViewModel.getUserInfo(inputHandle)
                    }
                )
            )
        }
    }

    companion object {
        const val TAG = "Login Activity"
    }
}
