package com.example.cfprogresstracker.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


fun sendEmailUsingIntent(
    context: Context,
    toSendEmail: Array<String>,
    emailSubject: String? = null,
    emailBody: String? = null,

){
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, toSendEmail)
        emailSubject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        emailBody?.let { putExtra(Intent.EXTRA_TEXT, it) }
    }

    if (intent.resolveActivity(context.applicationContext.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No Email Application Found", Toast.LENGTH_SHORT).show()
    }
}