package com.gourav.competrace.app_core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

fun loadUrl(context: Context, url: String?) {
    Log.e("Main Activity", "URL is $url")
    if(url.isNullOrBlank()) return
    val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(urlIntent)
}