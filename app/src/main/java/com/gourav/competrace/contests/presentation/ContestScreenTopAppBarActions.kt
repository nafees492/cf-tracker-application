package com.gourav.competrace.contests.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.components.CompetraceIconButton
import com.gourav.competrace.app_core.util.TimeUtils

@ExperimentalAnimationApi
@Composable
fun ContestScreenActions(
    openSettings: () -> Unit,
    openSite: () -> Unit
) {
    val todaysDate by remember {
        mutableStateOf(TimeUtils.todaysDate())
    }

    Text(
        text = todaysDate,
        style = MaterialTheme.typography.labelLarge
    )

    CompetraceIconButton(
        iconId = R.drawable.ic_baseline_settings_24px,
        onClick = openSettings,
        contentDescription = stringResource(id = R.string.settings)
    )

    CompetraceIconButton(
        iconId = R.drawable.ic_open_in_browser_24px,
        onClick = openSite,
        contentDescription = stringResource(id = R.string.open_in_browser)
    )

}


