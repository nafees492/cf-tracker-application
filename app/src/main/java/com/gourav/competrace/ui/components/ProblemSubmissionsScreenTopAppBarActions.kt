package com.gourav.competrace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.gourav.competrace.R
import com.gourav.competrace.utils.UserSubmissionFilter

@ExperimentalAnimationApi
@Composable
fun RowScope.ProblemSubmissionsScreenActions(
    currentSelectionForUserSubmissions: String,
    onClickAll: () -> Unit,
    onClickCorrect: () -> Unit,
    onClickIncorrect: () -> Unit
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

    IconButton(onClick = {
        expanded = true
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list_24px),
            contentDescription = "",
        )
    }

    AnimatedVisibility(visible = expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(UserSubmissionFilter.ALL) },
                onClick = {
                    onClickAll()
                    expanded = false
                },
                trailingIcon = {
                    trailingIcon(currentSelectionForUserSubmissions == UserSubmissionFilter.ALL)
                }
            )
            DropdownMenuItem(
                text = { Text(UserSubmissionFilter.CORRECT) },
                onClick = {
                    onClickCorrect()
                    expanded = false
                },
                trailingIcon = {
                    trailingIcon(currentSelectionForUserSubmissions == UserSubmissionFilter.CORRECT)
                }
            )
            DropdownMenuItem(
                text = { Text(UserSubmissionFilter.INCORRECT) },
                onClick = {
                    onClickIncorrect()
                    expanded = false
                },
                trailingIcon = {
                    trailingIcon(currentSelectionForUserSubmissions == UserSubmissionFilter.INCORRECT)
                }
            )
        }
    }
}


