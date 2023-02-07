package com.gourav.competrace.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString

fun copyTextToClipBoard(
    context: Context,
    text: String?,
    toastMessage: String? = null,
    clipboardManager: ClipboardManager,
    haptic: HapticFeedback
) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    text?.let { clipboardManager.setText(AnnotatedString(text)) }
    toastMessage?.let {  Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
}