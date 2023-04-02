package com.gourav.competrace.app_core.util

import androidx.annotation.StringRes
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.material3.SnackbarDuration

data class SnackbarMessage(
    val id: Long,
    val message: UiText,
    val actionLabel: UiText? = null,
    val action: () -> Unit = {},
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    private val messagesFlow: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val messages: StateFlow<SnackbarMessage?> get() = messagesFlow.asStateFlow()

    fun showMessage(
        message: UiText,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        messagesFlow.update {
             SnackbarMessage(
                id = UUID.randomUUID().mostSignificantBits,
                message = message,
                duration = duration
            )
        }
    }

    fun showMessageWithAction(
        messageTextId: UiText,
        actionLabelId: UiText,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit
    ) {
        messagesFlow.update {
            SnackbarMessage(
                id = UUID.randomUUID().mostSignificantBits,
                message = messageTextId,
                actionLabel = actionLabelId,
                action = action,
                duration = duration
            )
        }
    }

    fun setMessageShown(messageId: Long) {
        messagesFlow.update {null }
    }
}
