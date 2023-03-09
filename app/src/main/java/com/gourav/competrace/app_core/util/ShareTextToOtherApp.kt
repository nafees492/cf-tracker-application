package com.gourav.competrace.app_core.util

import android.content.Context
import android.content.Intent

fun Context.shareTextToOtherApp(textToShare: String?, heading: String? = null){
    val intent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textToShare)
        type = "text/*"
    }

    val shareIntent = Intent.createChooser(intent, heading)
    startActivity(shareIntent)
}