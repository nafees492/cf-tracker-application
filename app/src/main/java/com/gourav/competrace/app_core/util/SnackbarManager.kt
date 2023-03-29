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
    @StringRes val messageId: Int,
    @StringRes val actionLabelId: Int? = null,
    val action: () -> Unit = {},
    val duration: SnackbarDuration = SnackbarDuration.Short
)

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    private val messagesFlow: MutableStateFlow<List<SnackbarMessage>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<SnackbarMessage>> get() = messagesFlow.asStateFlow()

    fun showMessage(
        @StringRes messageTextId: Int,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        messagesFlow.update { currentMessages ->
            currentMessages + SnackbarMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageTextId,
                duration = duration
            )
        }
    }

    fun showMessageWithAction(
        @StringRes messageTextId: Int,
        @StringRes actionLabelId: Int,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit
    ) {
        messagesFlow.update { currentMessages ->
            currentMessages + SnackbarMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageTextId,
                actionLabelId = actionLabelId,
                action = action,
                duration = duration
            )
        }
    }

    fun setMessageShown(messageId: Long) {
        messagesFlow.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
