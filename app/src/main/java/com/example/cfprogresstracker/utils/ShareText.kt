package com.example.cfprogresstracker.utils

import android.content.Context
import android.content.Intent

fun shareTextToOtherApp(text: String, context: Context){
    val intent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(intent, null)
    context.startActivity(shareIntent)

}