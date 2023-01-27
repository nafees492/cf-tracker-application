package com.gourav.competrace.utils

import android.content.Context
import android.content.Intent

fun shareTextToOtherApp(text: String, context: Context){
    val intent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/*"
    }

    val shareIntent = Intent.createChooser(intent, "Share Link Via:")
    context.startActivity(shareIntent)
}