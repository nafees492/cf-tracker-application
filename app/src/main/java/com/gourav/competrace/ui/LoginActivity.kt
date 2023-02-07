package com.gourav.competrace.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.R
import com.gourav.competrace.data.UserPreferences
import com.gourav.competrace.retrofit.util.ApiState
import com.gourav.competrace.ui.components.CircularProgressIndicator
import com.gourav.competrace.ui.components.NormalButton
import com.gourav.competrace.ui.theme.CompetraceTheme
import com.gourav.competrace.ui.theme.DarkModePref
import com.gourav.competrace.viewmodel.MainViewModel
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

            val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = CompetraceTheme.DEFAULT)
            val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)

            CompetraceTheme(currentTheme = currentTheme, darkModePref = darkModePref) {
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
                    LoginScreen(inputHandle = inputHandle, onValueChange = onInputChange)
                }
                is ApiState.Loading -> {
                    CircularProgressIndicator(isDisplayed = true)
                }
                is ApiState.Success<*> -> {
                    if (apiResult.response.status == "OK") {
                        coroutineScope.launch(Dispatchers.IO) {
                            userPreferences.setHandleName(inputHandle.trim())
                        }
                    } else {
                        Log.e(TAG, apiResult.response.comment.toString())
                        Toast.makeText(this, apiResult.response.comment, Toast.LENGTH_SHORT).show()
                        mainViewModel.responseForUserInfo = ApiState.Empty
                    }
                }
                is ApiState.Failure -> {
                    Log.e(TAG, apiResult.msg.toString())
                    Toast.makeText(this, apiResult.msg.toString(), Toast.LENGTH_SHORT).show()
                    mainViewModel.responseForUserInfo = ApiState.Empty
                }
                else -> {}
            }
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent).also {
                Log.d(TAG, intent.toString())
                this.finish()
                Log.d(TAG, "Finished")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun LoginScreen(
        inputHandle: String,
        onValueChange: (String) -> Unit
    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        val keyboardController = LocalSoftwareKeyboardController.current

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = screenHeight * 0.35f, bottom = screenHeight * 0.1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.competrace_logo_96),
                            contentDescription = "Competrace Logo",
                            modifier = Modifier.size(72.dp),
                        )

                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = inputHandle,
                            onValueChange = onValueChange,
                            label = {
                                Text(
                                    text = "Enter Codeforces Handle",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_leaderboard_24),
                                    contentDescription = "Codeforces Logo"
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        NormalButton(
                            text = "Login",
                            onClick = {
                                if (inputHandle.isNotBlank()) mainViewModel.getUserInfo(inputHandle.trim())
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }
        }
    }

    companion object {
        const val TAG = "Login Activity"
    }

}
