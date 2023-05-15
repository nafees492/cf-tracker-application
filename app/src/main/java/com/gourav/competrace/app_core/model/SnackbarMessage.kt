package com.gourav.competrace.app_core.model

import androidx.compose.material3.SnackbarDuration
import com.gourav.competrace.app_core.util.UiText

data class SnackbarMessage(
    val id: Long,
    val message: UiText,
    val actionLabel: UiText? = null,
    val action: () -> Unit = {},
    val duration: SnackbarDuration = SnackbarDuration.Short,
)
