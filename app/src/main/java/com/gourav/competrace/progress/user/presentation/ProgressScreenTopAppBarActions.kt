package com.gourav.competrace.progress.user.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.gourav.competrace.R

@ExperimentalAnimationApi
@Composable
fun RowScope.ProgressScreenActions(
    onClickSettings: () -> Unit,
    onClickLogOut: () -> Unit,
    isLogOutButtonEnabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = onClickSettings) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_24px),
            contentDescription = stringResource(id = R.string.settings),
        )
    }

    IconButton(onClick = {
        expanded = true
    }) {
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = stringResource(R.string.more),
        )
    }
    AnimatedVisibility(
        visible = expanded,
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.logout)) },
                onClick = {
                    onClickLogOut()
                    expanded = false
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout_24px),
                        contentDescription = stringResource(id = R.string.logout),
                    )
                },
                enabled = isLogOutButtonEnabled
            )
        }
    }
}


