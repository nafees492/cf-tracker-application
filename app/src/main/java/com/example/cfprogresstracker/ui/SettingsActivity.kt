package com.example.cfprogresstracker.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cfprogresstracker.BuildConfig
import com.example.cfprogresstracker.R
import com.example.cfprogresstracker.data.UserPreferences
import com.example.cfprogresstracker.ui.components.AppDialog
import com.example.cfprogresstracker.ui.components.RadioButtonSelectionForAppTheme
import com.example.cfprogresstracker.ui.components.RowWithLeadingIcon
import com.example.cfprogresstracker.ui.components.SettingsSection
import com.example.cfprogresstracker.ui.theme.AppTheme
import com.example.cfprogresstracker.ui.theme.CodeforcesProgressTrackerTheme
import com.example.cfprogresstracker.utils.loadUrl
import com.example.cfprogresstracker.utils.sendEmailUsingIntent
import com.example.cfprogresstracker.utils.shareTextToOtherApp
import com.example.cfprogresstracker.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var userPreferences: UserPreferences
    private lateinit var coroutineScope: CoroutineScope

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            coroutineScope = rememberCoroutineScope()
            userPreferences = UserPreferences(LocalContext.current.applicationContext)

            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

            val navigationIcon: @Composable (() -> Unit) = {
                IconButton(onClick = { this.finish() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back Button"
                    )
                }
            }

            val topBar: @Composable () -> Unit = {
                TopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
            val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = AppTheme.SystemDefault.name)

            CodeforcesProgressTrackerTheme(currentTheme = currentTheme!!) {
                // A surface container using the 'background' color from the theme
                Surface {
                    Scaffold(
                        topBar = topBar
                    ) {
                        SettingsScreen(paddingValues = it, userPreferences = userPreferences)
                    }
                }
            }
        }
    }
    
    companion object {
        const val TAG = "Settings Activity"
    }
}

@SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState")
@Composable
fun SettingsScreen(paddingValues: PaddingValues, userPreferences: UserPreferences) {
    val isAbove12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val coroutineScope = rememberCoroutineScope()
    val currentTheme by userPreferences.currentThemeFlow.collectAsState(initial = AppTheme.SystemDefault.name)
    val context = LocalContext.current
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior

    val themeOptions = arrayListOf(AppTheme.SystemDefault, AppTheme.Light, AppTheme.Dark)
    if (isAbove12) themeOptions.add(AppTheme.Dynamic)

    val onClickSet: (String) -> Unit = {
        coroutineScope.launch(Dispatchers.IO) {
            userPreferences.setCurrentTheme(it)
        }
    }

    var openThemeDialog by remember { mutableStateOf(false) }
    val selectedTheme = mutableStateOf(currentTheme!!)

    val isOptionSelected: (AppTheme) -> Boolean = {
        it.name == selectedTheme.value
    }

    val onClickOption: (AppTheme) -> Unit = {
        selectedTheme.value = it.name
    }

    val appLink = stringResource(id = R.string.app_link_on_playstore)
    val feedbackEmailAddress = stringResource(id = R.string.feedback_email_address)
    val feedbackEmailSubject = stringResource(id = R.string.feedback_email_subject)
    val feedbackEmailBody = stringResource(id = R.string.feedback_email_body)

    AppDialog(
        openDialog = openThemeDialog,
        title = "Theme",
        iconId = R.drawable.ic_palette_24px,
        confirmButtonText = "Set",
        onClickConfirmButton = { onClickSet(selectedTheme.value) },
        dismissDialog = { openThemeDialog = false }) {
        Column {

            RadioButtonSelectionForAppTheme(
                themeOptions = themeOptions,
                isOptionSelected = isOptionSelected,
                onClickOption = onClickOption
            )

            if(isAbove12) Text(
                text = "Dynamic Theme selects theme colors from your Wallpaper Color Palate.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(8.dp)
            )
        }

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
    ) {

        item {
            SettingsSection(title = "Theme") {
                RowWithLeadingIcon(
                    title = "Theme",
                    leadingIconId = R.drawable.ic_palette_24px,
                    onClick = { openThemeDialog = true },
                    subTitle = currentTheme
                )
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }

        item {
            SettingsSection(title = "Social") {
                RowWithLeadingIcon(
                    title = "Share With Friends",
                    leadingIconId = R.drawable.ic_share_24px,
                    onClick = {
                        shareTextToOtherApp(
                            text = appLink,
                            context = context
                        )
                    },
                )
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }

        item {
            SettingsSection(title = "Information") {
                RowWithLeadingIcon(
                    title = "About App",
                    leadingIconId = R.drawable.ic_info_24px,
                    onClick = { /* TODO */ },
                    subTitle = "Version: ${BuildConfig.VERSION_NAME}"
                )
                
                RowWithLeadingIcon(
                    title = "Rate and Review",
                    leadingIconId = R.drawable.ic_star_half_24px,
                    onClick = {
                        loadUrl(context = context, url = appLink)
                    },
                )
                RowWithLeadingIcon(
                    title = "Feedback",
                    leadingIconId = R.drawable.ic_forum_24px,
                    onClick = {
                        sendEmailUsingIntent(
                            context = context,
                            toSendEmail = arrayOf(feedbackEmailAddress),
                            emailSubject = feedbackEmailSubject,
                            emailBody = feedbackEmailBody
                        )
                    },
                )
                RowWithLeadingIcon(
                    title = "Developers Contact",
                    leadingIconId = R.drawable.ic_code_24px,
                    onClick = { /* TODO */ },
                    subTitle = feedbackEmailAddress
                )
            }
        }
    }
}