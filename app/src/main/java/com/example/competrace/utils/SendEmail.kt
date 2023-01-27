package com.example.competrace.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


fun sendEmail(
    context: Context,
    toSendEmail: Array<String>,
    emailSubject: String? = null,
    emailBody: String? = null,

    ) {
    val mailIntent = Intent(Intent.ACTION_VIEW)
    val data =
        Uri.parse("mailto:?subject=" + "$emailSubject" + "&body=" + "$emailBody" + "&to=" + toSendEmail[0])
    mailIntent.data = data
    context.startActivity(Intent.createChooser(mailIntent, "Send mail..."))

}