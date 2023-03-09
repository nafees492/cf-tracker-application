package com.gourav.competrace.app_core.util

import androidx.annotation.StringRes
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SnackbarMessage(
    val id: Long,
    @StringRes val messageId: Int,
    @StringRes val actionLabelId: Int? = null,
    val action: () -> Unit = {}
)

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
object SnackbarManager {

    private val _messages: MutableStateFlow<List<SnackbarMessage>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<SnackbarMessage>> get() = _messages.asStateFlow()

    fun showMessage(@StringRes messageId: Int) {
        _messages.update { currentMessages ->
            currentMessages + SnackbarMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageId
            )
        }
    }

    fun showMessageWithAction(
        @StringRes messageTextId: Int,
        @StringRes actionLabelId: Int,
        action: () -> Unit
    ) {
        _messages.update { currentMessages ->
            currentMessages + SnackbarMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = messageTextId,
                actionLabelId = actionLabelId,
                action = action
            )
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}
