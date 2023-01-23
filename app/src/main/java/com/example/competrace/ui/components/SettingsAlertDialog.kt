package com.example.competrace.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.competrace.BuildConfig
import com.example.competrace.R
import com.example.competrace.data.UserPreferences
import com.example.competrace.ui.theme.DarkModePref
import com.example.competrace.ui.theme.MyTheme
import com.example.competrace.utils.loadUrl
import com.example.competrace.utils.sendEmail
import com.example.competrace.utils.shareTextToOtherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = MyTheme.DEFAULT)
    val darkModePref by userPreferences.darkModePrefFlow.collectAsState(initial = DarkModePref.SYSTEM_DEFAULT)

    val context = LocalContext.current

    val themeOptions = arrayListOf(MyTheme.DEFAULT)
    if (isAbove12) themeOptions.add(MyTheme.DYNAMIC)

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

    val isThemeOptionSelected: (String) -> Boolean = {
        it == currentTheme!!
    }

    val isDarkModePrefOptionSelected: (String) -> Boolean = {
        it == darkModePref!!
    }

    val appLink = stringResource(id = R.string.app_link_on_playstore)
    val feedbackEmailAddress = stringResource(id = R.string.feedback_email_address)
    val feedbackEmailSubject = stringResource(id = R.string.feedback_email_subject)
    val feedbackEmailBody = stringResource(id = R.string.feedback_email_body)

    AppDialog(
        openDialog = openSettingsDialog,
        title = "Settings",
//        iconId = R.drawable.ic_baseline_settings_24px,
        confirmButtonText = "OK",
        onClickConfirmButton = dismissSettingsDialogue,
        dismissDialog = dismissSettingsDialogue
    ) {
        Column {
            Divider(
                modifier = Modifier.padding(vertical = 4.dp)
            )
            if (isAbove12) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                RadioButtonSelectionForAppTheme(
                    themeOptions = themeOptions,
                    isOptionSelected = isThemeOptionSelected,
                    onClickOption = setTheme
                )
            }

            Text(
                text = "Dark mode preference",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            RadioButtonSelectionForDarkModePref(
                darkModePrefOptions = darkModePrefOptions,
                isOptionSelected = isDarkModePrefOptionSelected,
                onClickOption = setDarkModePref,
            )
            Divider(
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NormalIconButton(
                    iconId = R.drawable.ic_info_24px,
                    onClick = { /*TODO*/ },
                    text = "About",
                    contentDescription = "About",
                )
                NormalIconButton(
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
                NormalIconButton(
                    iconId = R.drawable.ic_star_half_24px,
                    onClick = {
                        loadUrl(context = context, url = appLink)
                    },
                    text = "Rate and Review",
                    contentDescription = "Rate and Review"
                )
                NormalIconButton(
                    iconId = R.drawable.ic_share_24px,
                    onClick = {
                        shareTextToOtherApp(
                            text = appLink,
                            context = context
                        )
                    },
                    text = "Share",
                    contentDescription = "Share with friends"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Version: $versionName",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }

}