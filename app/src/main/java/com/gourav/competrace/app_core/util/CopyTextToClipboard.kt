package com.gourav.competrace.app_core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.gourav.competrace.R

fun Context.copyTextToClipBoard(
    textToCopy: String?,
    @StringRes toastMessageId: Int,
    clipboardManager: ClipboardManager,
    haptic: HapticFeedback
) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    textToCopy?.let { clipboardManager.setText(AnnotatedString(textToCopy)) }
    SnackbarManager.showMessageWithAction(
        toastMessageId,
        R.string.share
    ) {
        shareTextToOtherApp(getString(R.string.share_copied_link, textToCopy))
    }
}