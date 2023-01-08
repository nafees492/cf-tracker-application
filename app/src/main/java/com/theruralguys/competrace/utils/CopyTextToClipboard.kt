package com.theruralguys.competrace.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString

fun copyTextToClipBoard(
    text: String,
    context: Context,
    clipboardManager: ClipboardManager,
    haptic: HapticFeedback
) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    clipboardManager.setText(AnnotatedString(text))
    Toast.makeText(context, "Problem Link Copied", Toast.LENGTH_SHORT).show()
}