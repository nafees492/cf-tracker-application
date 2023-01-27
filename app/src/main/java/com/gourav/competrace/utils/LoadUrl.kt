package com.gourav.competrace.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

fun loadUrl(context: Context, url: String) {
    Log.e("Main Activity", "URL is $url")
    val urlIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )
    context.startActivity(urlIntent)
}