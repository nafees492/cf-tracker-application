package com.gourav.competrace.app_core.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.ContextCompat
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
        UiText.StringResource(toastMessageId),
        UiText.StringResource(R.string.share)
    ) {
        shareTextToOtherApp(
            textToShare = UiText.StringResource(
                R.string.share_copied_link,
                textToCopy.toString()
            )
        )
    }
}

fun Context.shareTextToOtherApp(textToShare: UiText, heading: UiText? = null) {
    val intent: Intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, textToShare.asString(this@shareTextToOtherApp))
        type = "text/*"
    }

    val shareIntent = Intent.createChooser(intent, heading?.asString(this))
    startActivity(shareIntent)
}

fun Context.loadUrl(url: String?) {
    Log.d("Load URL", "URL is $url")

    if (url.isNullOrBlank()) {
        SnackbarManager.showMessage(UiText.StringResource(R.string.url_not_set))
        return
    }

    try {
        CustomTabsIntent.Builder().build().launchUrl(
            this, Uri.parse(url)
        )
    } catch (e: Exception){
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

}

fun Context.sendEmail(
    toSendEmail: Array<String>,
    emailSubject: String? = null,
    emailBody: String? = null,
) {
    Intent(Intent.ACTION_VIEW).apply {
        data =
            Uri.parse("mailto:?subject=" + "$emailSubject" + "&body=" + "$emailBody" + "&to=" + toSendEmail[0])
    }.also {
        startActivity(Intent.createChooser(it, getString(R.string.send_mail)))
    }
}

fun Context.addEventToCalendar(
    title: String,
    startTime: Long,
    endTime: Long,
    location: String,
    description: String
) {
    Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title) // Simple title
        putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        putExtra(
            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            startTime
        ) // Only date part is considered when ALL_DAY is true; Same as DTSTART
        putExtra(
            CalendarContract.EXTRA_EVENT_END_TIME,
            endTime
        ) // Only date part is considered when ALL_DAY is true
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.Events.DESCRIPTION, description) // Description
        putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE)
        //.putExtra(Intent.EXTRA_EMAIL, "fooInviteeOne@gmail.com,fooInviteeTwo@gmail.com")
        //.putExtra(CalendarContract.Events.RRULE, getRRule()) // Recurrence rule
    }.also(this::startActivity)
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}