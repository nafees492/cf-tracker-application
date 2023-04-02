package com.gourav.competrace.progress.user.presentation.login

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceButton
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton

private const val TAG = "Login Screen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val users by loginViewModel.userList.collectAsState()

    val inputHandle by loginViewModel.inputHandle.collectAsState()
    val isValidHandle by loginViewModel.isValidHandle.collectAsState()

    val bottomPadding by animateDpAsState(targetValue = if (users.isEmpty()) 80.dp else 16.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            LazyColumn(
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.animateContentSize(animationSpec = tween())
            ) {
                item(key = "recent-logins") {
                    if (users.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_history_24px),
                                contentDescription = null,
                            )
                            Text(
                                text = "Recent Logins",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.competrace_shadowed_480),
                                contentDescription = stringResource(id = R.string.app_name),
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
                }

                items(users.size, key = { users[it].handle }) {
                    UserCard(
                        handle = users[it].handle,
                        onClick = loginViewModel::setUser,
                        onLongClick = {},
                        onClickCancelIcon = loginViewModel::deleteUserFromDatabase,
//                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(top = 8.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputHandle,
                onValueChange = loginViewModel::onInputHandleChange,
                label = {
                    Text(
                        text = stringResource(id = R.string.enter_codeforces_handle),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.logo_codeforces_white_96),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(CircleShape)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            CompetraceButton(
                text = stringResource(id = R.string.login),
                onClick = {
                    loginViewModel.checkUsernameAvailable(inputHandle.trim())
                },
                enabled = isValidHandle,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}