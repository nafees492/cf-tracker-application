package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import com.example.cfprogresstracker.R

@ExperimentalAnimationApi
@Composable
fun RowScope.ProgressScreenActions(
    onClickLogOut: () -> Unit,
    onClickRefresh: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = {
        expanded = true
    }) {
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = "",
        )
    }
    AnimatedVisibility(visible = expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Refresh") },
                onClick = onClickRefresh,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_refresh_24px),
                        contentDescription = "Refresh",
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Log Out") },
                onClick = onClickLogOut,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout_24px),
                        contentDescription = "Log Out",
                    )
                }
            )
        }
    }
}


