package com.gourav.competrace.app_core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompetraceAlertDialog(
    openDialog: Boolean,
    title: String,
    confirmButtonText: String,
    onClickConfirmButton: () -> Unit,
    dismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    iconId: Int? = null,
    dismissButtonText: String? = null,
    content: @Composable () -> Unit
) {
    val dismissButton: @Composable (() -> Unit)? = dismissButtonText?.let {
        {
            TextButton(onClick = dismissDialog) { Text(it) }
        }
    }

    val icon: @Composable (() -> Unit)? = iconId?.let {
        {
            Icon(painter = painterResource(id = it), contentDescription = null)
        }
    }

    AnimatedVisibility(
        visible = openDialog,
        enter = scaleIn() + fadeIn(initialAlpha = 0.4f),
        exit = scaleOut() + fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = dismissDialog,
            icon = icon,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    textAlign = TextAlign.Center
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompetenceConfirmationDialog(
    isDialogOpen: Boolean,
    title: String,
    description: String,
    confirmButtonText: String,
    onClickConfirmButton: () -> Unit,
    dismissButtonText: String,
    dismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    iconId: Int? = null
) {
    val icon: @Composable (() -> Unit)? = iconId?.let {
        {
            Icon(painter = painterResource(id = it), contentDescription = null)
        }
    }

    AnimatedVisibility(
        visible = isDialogOpen,
        enter = scaleIn() + fadeIn(initialAlpha = 0.4f),
        exit = scaleOut() + fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = dismissDialog,
            icon = icon,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onClickConfirmButton()
                    dismissDialog()
                }) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = dismissDialog) { Text(dismissButtonText) }
            },
            shape = MaterialTheme.shapes.large.copy(CornerSize(16.dp)),
            modifier = modifier
        )
    }
}