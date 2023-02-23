package com.gourav.competrace.app_core.util

import android.content.Context
import android.content.Intent

fun shareTextToOtherApp(context: Context, text: String, heading: String? = null){
    val intent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/*"
    }

    val shareIntent = Intent.createChooser(intent, heading)
    context.startActivity(shareIntent)
}