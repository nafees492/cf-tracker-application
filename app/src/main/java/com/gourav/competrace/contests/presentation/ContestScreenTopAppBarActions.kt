package com.gourav.competrace.contests.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.gourav.competrace.R
import com.gourav.competrace.app_core.util.getTodaysDate

@ExperimentalAnimationApi
@Composable
fun ContestScreenActions(
    onClickSettings: () -> Unit,
) {
    val todaysDate by remember {
        mutableStateOf(getTodaysDate())
    }

    Text(
        text = todaysDate
    )

    IconButton(onClick = onClickSettings) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_24px),
            contentDescription = "Settings",
        )
    }
}


