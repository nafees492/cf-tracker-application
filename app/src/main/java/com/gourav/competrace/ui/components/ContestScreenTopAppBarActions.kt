package com.gourav.competrace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gourav.competrace.R
import com.gourav.competrace.utils.FinishedContestFilter

@ExperimentalAnimationApi
@Composable
fun RowScope.ContestScreenActions(
    onClickSettings: () -> Unit,
    currentSelectionForFinishedContests: String,
    onClickAll: () -> Unit,
    onClickGiven: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val trailingIcon: @Composable ((visible: Boolean) -> Unit) = {
        AnimatedVisibility(visible = it) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = ""
            )
        }
    }

    IconButton(onClick = onClickSettings) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_settings_24px),
            contentDescription = "Settings",
        )
    }

    IconButton(onClick = {
        expanded = true
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list_24px),
            contentDescription = "",
        )
    }
    AnimatedVisibility(
        visible = expanded,
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Text(
                text = "Finished Contests",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(8.dp)
            )
            Divider()
            DropdownMenuItem(
                text = { Text(FinishedContestFilter.GIVEN) },
                onClick = {
                    onClickGiven()
                    expanded = false
                },
                trailingIcon = {
                    trailingIcon(currentSelectionForFinishedContests == FinishedContestFilter.GIVEN)
                }
            )
            DropdownMenuItem(
                text = { Text(FinishedContestFilter.ALL) },
                onClick = {
                    onClickAll()
                    expanded = false
                },
                trailingIcon = {
                    trailingIcon(currentSelectionForFinishedContests == FinishedContestFilter.ALL)
                }
            )
        }
    }
}


