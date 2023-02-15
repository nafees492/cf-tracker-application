package com.gourav.competrace.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gourav.competrace.BuildConfig
import com.gourav.competrace.R
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.ui.theme.CompetraceTheme
import com.gourav.competrace.ui.theme.DarkModePref
import com.gourav.competrace.utils.loadUrl
import com.gourav.competrace.utils.sendEmail
import com.gourav.competrace.utils.shareTextToOtherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsAlertDialog(
    openSettingsDialog: Boolean,
    dismissSettingsDialogue: () -> Unit,
    userPreferences: UserPreferences
) {

    val isAbove12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val coroutineScope = rememberCoroutineScope()
    val versionName = BuildConfig.VERSION_NAME

    val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = CompetraceTheme.DEFAULT)
    val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)
    val showTagsInProblemSet by userPreferences.showTagsFlow.collectAsState(initial = true)

    val context = LocalContext.current

    val themeOptions = arrayListOf(CompetraceTheme.DEFAULT)
    if (isAbove12) themeOptions.add(CompetraceTheme.DYNAMIC)

    val darkModePrefOptions =
        arrayListOf(DarkModePref.SYSTEM_DEFAULT, DarkModePref.LIGHT, DarkModePref.DARK)

    val setTheme: (String) -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            userPreferences.setCurrentTheme(it)
        }
    }

    val setDarkModePref: (String) -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            userPreferences.setDarkModePref(it)
        }
    }

    val setShowTagsInProblemSet: (Boolean) -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            userPreferences.setShowTagsFlow(it)
        }
    }

    val appLink = stringResource(id = R.string.app_link_on_playstore)
    val shareAppText = stringResource(id = R.string.share_app_text)
    val feedbackEmailAddress = stringResource(id = R.string.feedback_email_address)
    val feedbackEmailSubject = stringResource(id = R.string.feedback_email_subject)
    val feedbackEmailBody = stringResource(id = R.string.feedback_email_body)
    val privacyPolicyLink = stringResource(id = R.string.privacy_policy_link)

    AppDialog(
        openDialog = openSettingsDialog,
        title = "Settings",
//        iconId = R.drawable.ic_baseline_settings_24px,
        confirmButtonText = "OK",
        onClickConfirmButton = dismissSettingsDialogue,
        dismissDialog = dismissSettingsDialogue
    ) {
        LazyColumn {
            item {
                Text(
                    text = "General",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Show Tags",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Switch(
                        checked = showTagsInProblemSet,
                        onCheckedChange = setShowTagsInProblemSet
                    )
                }
            }

            item {
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (isAbove12) {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    RadioButtonSelectionForAppTheme(
                        themeOptions = themeOptions,
                        isOptionSelected = { it == currentTheme },
                        onClickOption = setTheme
                    )
                }
                Text(
                    text = "Dark mode preference",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                RadioButtonSelectionForDarkModePref(
                    darkModePrefOptions = darkModePrefOptions,
                    isOptionSelected = { it == darkModePref },
                    onClickOption = setDarkModePref,
                )
                Divider(
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CompetraceIconButton(
                        iconId = R.drawable.ic_shield_24px,
                        onClick = { loadUrl(context = context, url = privacyPolicyLink) },
                        text = "Privacy Policy",
                        contentDescription = "Privacy Policy",
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_report_24px,
                        onClick = {
                            sendEmail(
                                context = context,
                                toSendEmail = arrayOf(feedbackEmailAddress),
                                emailSubject = feedbackEmailSubject,
                                emailBody = feedbackEmailBody
                            )
                        },
                        text = "Report Issue",
                        contentDescription = "Feedback"
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_star_half_24px,
                        onClick = {
                            loadUrl(context = context, url = appLink)
                        },
                        text = "Rate and Review",
                        contentDescription = "Rate and Review"
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_share_24px,
                        onClick = {
                            shareTextToOtherApp(
                                context = context,
                                text = shareAppText,
                                heading = "Share \"Competrace\" Via:"
                            )
                        },
                        text = "Share",
                        contentDescription = "Share with friends"
                    )
                }
            }

            item {
                val text1 = AnnotatedString("< Version-$versionName />")

                val text2 = buildAnnotatedString {
                    append("Designed By: Vidhi Khosla - ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("vid2001work@gmail.com")
                    }
                    append("\nDeveloped By: Lokesh Patidar - ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("gourav.ranayara@gmail.com")
                    }
                }

                var version by remember {
                    mutableStateOf(text1)
                }
                AnimatedContent(targetState = version) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SelectionContainer {
                            CompetraceClickableText(
                                text = it,
                                onClick = { version = if (version == text1) text2 else text1 },
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

}