package com.example.cfprogresstracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun AppDialog(
    openDialog: Boolean,
    title: String,
    iconId: Int? = null,
    confirmButtonText: String,
    onClickConfirmButton: () -> Unit,
    dismissDialog: () -> Unit,
    dismissButtonText: String? = null,
    content: @Composable () -> Unit
) {


    val dismissButton: @Composable (() -> Unit)? = dismissButtonText?.let {
        {
            TextButton(
                onClick = {
                    dismissDialog()
                }
            ) {
                Text(it)
            }
        }
    }

    val icon: @Composable (() -> Unit)? = iconId?.let {
        {
            Icon(painter = painterResource(id = it), contentDescription = null)
        }
    }

    AnimatedVisibility (
        visible = openDialog,
        enter = fadeIn(animationSpec = tween(500), initialAlpha = 0f),
        exit = fadeOut(animationSpec = tween(5))
    ) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                dismissDialog()
            },
            icon = icon,
            title = {
                Text(text = title)
            },
            text = content,
            confirmButton = {
                TextButton(
                    onClick = {
                        onClickConfirmButton()
                        dismissDialog()
                    }
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = dismissButton
        )
    }
}