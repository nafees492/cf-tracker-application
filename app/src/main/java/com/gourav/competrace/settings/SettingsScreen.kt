package com.gourav.competrace.settings

import android.app.AlarmManager
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceClickableText
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton
import com.gourav.competrace.app_core.ui.theme.CompetraceThemeNames
import com.gourav.competrace.app_core.ui.theme.DarkModePref
import com.gourav.competrace.app_core.util.*
import com.gourav.competrace.contests.data.ContestContestAlarmSchedulerImpl
import com.gourav.competrace.contests.model.ContestAlarmItem
import com.gourav.competrace.contests.presentation.ContestViewModel
import com.gourav.competrace.app_core.util.sendEmail

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SettingsScreen(
    contestViewModel: ContestViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current

    val currentTheme by settingsViewModel.currentTheme.collectAsState(initial = CompetraceThemeNames.DEFAULT)
    val darkModePref by settingsViewModel.darkModePref.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)
    val showTags by settingsViewModel.showTags.collectAsState(initial = true)

    val scheduleNotifBefore by settingsViewModel.scheduleNotifBefore.collectAsState()

    val appLink = stringResource(id = R.string.app_link_on_playstore)
    val feedbackEmailAddress = stringResource(id = R.string.gourav_email)
    val feedbackEmailSubject = stringResource(id = R.string.feedback_email_subject)
    val feedbackEmailBody = stringResource(id = R.string.feedback_email_body)
    val privacyPolicyLink = stringResource(id = R.string.privacy_policy_link)

    var isClearAllSetNotificationsAlertOpen by rememberSaveable {
        mutableStateOf(false)
    }

    AlertClearAllSetNotifications(
        isOpen = isClearAllSetNotificationsAlertOpen,
        onClickConfirmButton = contestViewModel::clearAllNotifications,
        dismissDialog = { isClearAllSetNotificationsAlertOpen = false }
    )

    var isScheduleNotifBeforeAlertOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val alarmManager = context.getSystemService(AlarmManager::class.java)

    AlertScheduleNotifBefore(
        alarmManager = alarmManager,
        isOpen = isScheduleNotifBeforeAlertOpen,
        selectedOption = scheduleNotifBefore,
        onSelectOption = settingsViewModel::changeScheduleNotifBeforeTo,
        dismissDialog = { isScheduleNotifBeforeAlertOpen = false }
    )

    var isThemeAlertOpen by rememberSaveable {
        mutableStateOf(false)
    }

    if (settingsViewModel.isDeviceAbove12) AlertTheme(
        isOpen = isThemeAlertOpen,
        themeOptions = settingsViewModel.themeOptions,
        selectedOption = currentTheme,
        onSelectOption = settingsViewModel::setTheme,
        dismissDialog = { isThemeAlertOpen = false }
    )

    var isDarkModePrefAlertOpen by rememberSaveable {
        mutableStateOf(false)
    }

    AlertDarkModePref(
        isOpen = isDarkModePrefAlertOpen,
        darkModePrefOptions = settingsViewModel.darkModePrefOptions,
        selectedOption = darkModePref,
        onSelectOption = settingsViewModel::setDarkModePref,
        dismissDialog = { isDarkModePrefAlertOpen = false }
    )

    var isTestNotifAlertOpen by rememberSaveable {
        mutableStateOf(false)
    }

    AlertTestNotif(
        isOpen = isTestNotifAlertOpen,
        onSelectOk = {
            ContestContestAlarmSchedulerImpl(context).schedule(
                ContestAlarmItem(
                    id = 0,
                    contestId = "test-notification-alarm",
                    timeInMillis = TimeUtils.currentTimeInMillis() + TimeUtils.minutesToMillis(it),
                    title = Sites.Codeforces.title,
                    message = buildString {
                        append("{Contest.name}")
                        append(" is going to start at ")
                        append("{startTime}")
                        append(". Hurry Up!!\n")
                        append("{timeLeft}")
                        append(" to Go.\n")
                    },
                    registrationUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
                )
            )

            SnackbarManager.showMessage(UiText.StringResource(R.string.conf_test_notif_set, it))
        },
        dismissDialog = { isTestNotifAlertOpen = false }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            SettingsSection(title = stringResource(id = R.string.general)) {
                RowWithLeadingIcon(
                    title = stringResource(id = R.string.show_tags),
                    leadingIconId = R.drawable.ic_tag_24px,
                    onClick = { settingsViewModel.setShowTags(!showTags) },
                    switchState = showTags
                )


                    RowWithLeadingIcon(
                        title = stringResource(id = R.string.schedule_notif_before),
                        leadingIconId = R.drawable.ic_schedule_24px,
                        subTitle = ScheduleNotifBeforeOptions.values()
                            .find { it.value == scheduleNotifBefore }?.option,
                        onClick = { isScheduleNotifBeforeAlertOpen = true },
                        showError = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                            !alarmManager.canScheduleExactAlarms() else false
                    )


                RowWithLeadingIcon(
                    title = stringResource(id = R.string.clear_all_set_notif),
                    leadingIconId = R.drawable.ic_clear_all_24px,
                    onClick = { isClearAllSetNotificationsAlertOpen = true }
                )

                RowWithLeadingIcon(
                    title = stringResource(id = R.string.test_notif),
                    leadingIconId = R.drawable.ic_notifications_24px,
                    onClick = { isTestNotifAlertOpen = true }
                )
            }
        }

        item {
            SettingsSection(title = stringResource(id = R.string.theme)) {
                if (settingsViewModel.isDeviceAbove12) RowWithLeadingIcon(
                    title = stringResource(id = R.string.theme),
                    leadingIconId = R.drawable.ic_palette_24px,
                    subTitle = currentTheme,
                    onClick = { isThemeAlertOpen = true }
                )

                RowWithLeadingIcon(
                    title = stringResource(id = R.string.dark_mode_pref),
                    leadingIconId = R.drawable.ic_dark_mode_24px,
                    subTitle = darkModePref,
                    onClick = { isDarkModePrefAlertOpen = true }
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.information)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CompetraceIconButton(
                        iconId = R.drawable.ic_shield_24px,
                        onClick = { context.loadUrl(url = privacyPolicyLink) },
                        text = stringResource(id = R.string.privacy_policy),
                        contentDescription = stringResource(id = R.string.privacy_policy),
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_report_24px,
                        onClick = {
                            context.sendEmail(
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
                            context.loadUrl(url = appLink)
                        },
                        text = stringResource(id = R.string.rate_and_review),
                        contentDescription = stringResource(id = R.string.rate_and_review)
                    )
                    CompetraceIconButton(
                        iconId = R.drawable.ic_share_24px,
                        onClick = {
                            context.shareTextToOtherApp(
                                textToShare = UiText.StringResource(R.string.share_app_text),
                                heading = UiText.StringResource(R.string.share_competrace_via)
                            )
                        },
                        text = stringResource(id = R.string.share),
                        contentDescription = stringResource(id = R.string.share)
                    )
                }
            }

        }

        item {
            val text1 = AnnotatedString(
                stringResource(
                    id = R.string.version,
                    settingsViewModel.appVersionName
                )
            )

            val text2 = buildAnnotatedString {
                append(stringResource(id = R.string.designed_by))
                append("\n")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(id = R.string.vidhi_email))
                }
                append("\n")
                append(stringResource(id = R.string.developed_by))
                append("\n")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(id = R.string.gourav_email))
                }
            }

            var version by remember { mutableStateOf(text1) }
            AnimatedContent(targetState = version) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
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