package com.gourav.competrace.ui.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.util.ApiState
import com.gourav.competrace.login.LoginActivity
import com.gourav.competrace.app_core.MainActivity
import com.gourav.competrace.ui.components.CircularProgressIndicator
import com.gourav.competrace.ui.components.CompetraceButton
import com.gourav.competrace.utils.findActivity
import com.gourav.competrace.login.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)

    val userHandle by userPreferences.handleNameFlow.collectAsState(initial = "")

    if (userHandle.isNullOrBlank()) {
        when (val apiResult = loginViewModel.responseForCheckUsernameAvailable) {
            is ApiState.Empty -> {
                LoginScreenPresentation(loginViewModel = loginViewModel)
            }
            is ApiState.Loading -> {
                CircularProgressIndicator(isDisplayed = true, modifier = Modifier.fillMaxSize())
            }
            is ApiState.Success<*> -> {
                if (apiResult.response.status == "OK") {
                    LaunchedEffect(Unit) {
                        userPreferences.setHandleName(loginViewModel.inputHandle.value.trim())
                    }
                } else {
                    Log.e(LoginActivity.TAG, apiResult.response.comment.toString())
                    Toast.makeText(context, apiResult.response.comment, Toast.LENGTH_SHORT).show()
                    loginViewModel.responseForCheckUsernameAvailable = ApiState.Empty
                }
            }
            is ApiState.Failure -> {
                Log.e(LoginActivity.TAG, apiResult.msg.toString())
                Toast.makeText(context, apiResult.msg.toString(), Toast.LENGTH_SHORT).show()
                loginViewModel.responseForCheckUsernameAvailable = ApiState.Empty
            }
            else -> {}
        }
    } else {
        val intent = Intent(context, MainActivity::class.java)
        val parentActivity = context.findActivity()
        parentActivity.startActivity(intent).also {
            Log.d(LoginActivity.TAG, intent.toString())
            parentActivity.finish()
            Log.d(LoginActivity.TAG, "Finished")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreenPresentation(loginViewModel: LoginViewModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val keyboardController = LocalSoftwareKeyboardController.current

    val inputHandle by loginViewModel.inputHandle.collectAsState()

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
                    modifier = Modifier.size(96.dp),
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
                    onValueChange = loginViewModel::onInputHandleChange,
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

                CompetraceButton(
                    text = "Login",
                    onClick = {
                        if (inputHandle.isNotBlank()) loginViewModel.checkUsernameAvailable(inputHandle.trim())
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}