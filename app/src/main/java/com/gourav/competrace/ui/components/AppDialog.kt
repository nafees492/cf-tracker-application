package com.gourav.competrace.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDialog(
    openDialog: Boolean,
    title: String,
    iconId: Int? = null,
    confirmButtonText: String,
    onClickConfirmButton: () -> Unit,
    dismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
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

    AnimatedVisibility(
        visible = openDialog,
        enter = fadeIn(animationSpec = tween(800), initialAlpha = 0.4f),
        exit = fadeOut(animationSpec = tween(400))
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
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
            dismissButton = dismissButton,
            shape = MaterialTheme.shapes.large.copy(CornerSize(16.dp)),
            modifier = modifier
        )
    }
}