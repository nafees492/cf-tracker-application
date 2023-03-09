package com.gourav.competrace.settings

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
import com.gourav.competrace.app_core.ui.components.CompetraceClickableText
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton
import com.gourav.competrace.app_core.ui.theme.CompetraceThemeNames
import com.gourav.competrace.app_core.ui.theme.DarkModePref
import com.gourav.competrace.app_core.util.loadUrl
import com.gourav.competrace.utils.sendEmail
import com.gourav.competrace.app_core.util.shareTextToOtherApp
import com.gourav.competrace.ui.components.AppDialog
import com.gourav.competrace.ui.components.RadioButtonSelectionForDarkModePref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsAlertDialog(
    openSettingsDialog: Boolean,
    dismissSettingsDialogue: () -> Unit,
    userPreferences: UserPreferences = UserPreferences(LocalContext.current)
) {

    val isAbove12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val coroutineScope = rememberCoroutineScope()
    val versionName = BuildConfig.VERSION_NAME

    val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = CompetraceThemeNames.DEFAULT)
    val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)
    val showTagsInProblemSet by userPreferences.showTagsFlow.collectAsState(initial = true)

    val context = LocalContext.current

    val themeOptions = arrayListOf(CompetraceThemeNames.DEFAULT)
    if (isAbove12) themeOptions.add(CompetraceThemeNames.DYNAMIC)

    val darkModePrefOptions =
        listOf(DarkModePref.SYSTEM_DEFAULT, DarkModePref.LIGHT, DarkModePref.DARK)

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
            userPreferences.setShowTags(it)
        }
    }

    val appLink = stringResource(id = R.string.app_link_on_playstore)
    val shareAppText = stringResource(id = R.string.share_app_text)
    val feedbackEmailAddress = stringResource(id = R.string.gourav_email)
    val feedbackEmailSubject = stringResource(id = R.string.feedback_email_subject)
    val feedbackEmailBody = stringResource(id = R.string.feedback_email_body)
    val privacyPolicyLink = stringResource(id = R.string.privacy_policy_link)

    AppDialog(
        openDialog = openSettingsDialog,
        title = stringResource(id = R.string.settings),
//        iconId = R.drawable.ic_baseline_settings_24px,
        confirmButtonText = stringResource(id = R.string.ok),
        onClickConfirmButton = dismissSettingsDialogue,
        dismissDialog = dismissSettingsDialogue
    ) {
        LazyColumn {
            item {
                Text(
                    text = stringResource(id = R.string.general),
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
                        text = stringResource(id = R.string.show_tags),
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
                        text = stringResource(id = R.string.theme),
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
                    text = stringResource(id = R.string.dark_mode_pref),
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
                        text = stringResource(id = R.string.privacy_policy),
                        contentDescription = stringResource(id = R.string.privacy_policy),
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
                        text = stringResource(id = R.string.report_issue),
                        contentDescription = stringResource(id = R.string.report_issue)
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_star_half_24px,
                        onClick = {
                            loadUrl(context = context, url = appLink)
                        },
                        text = stringResource(id = R.string.rate_and_review),
                        contentDescription = stringResource(id = R.string.rate_and_review)
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_share_24px,
                        onClick = {
                            context.shareTextToOtherApp(
                                textToShare = shareAppText,
                                heading = context.getString(R.string.share_competrace_via)
                            )
                        },
                        text = stringResource(id = R.string.share),
                        contentDescription = stringResource(id = R.string.share)
                    )
                }
            }

            item {
                val text1 = AnnotatedString(stringResource(id = R.string.version, versionName))

                val text2 = buildAnnotatedString {
                    append(stringResource(id = R.string.designed_by))
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(id = R.string.vidhi_email))
                    }
                    append("\n")
                    append(stringResource(id = R.string.developed_by))
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(id = R.string.gourav_email))
                    }
                }

                var version by remember { mutableStateOf(text1) }
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