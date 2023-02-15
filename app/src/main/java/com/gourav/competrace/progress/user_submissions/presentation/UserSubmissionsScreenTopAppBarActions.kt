package com.gourav.competrace.progress.user_submissions.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.gourav.competrace.R
import com.gourav.competrace.ui.components.CompetraceIconButton
import com.gourav.competrace.utils.UserSubmissionFilter

@ExperimentalAnimationApi
@Composable
fun RowScope.UserSubmissionsScreenActions(
    currentSelectionForUserSubmissions: String,
    onClickSearch: () -> Unit,
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

    CompetraceIconButton(iconId = R.drawable.ic_search_24px, onClick = onClickSearch)

    CompetraceIconButton(iconId = R.drawable.ic_filter_list_24px, onClick = { expanded = true })

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


