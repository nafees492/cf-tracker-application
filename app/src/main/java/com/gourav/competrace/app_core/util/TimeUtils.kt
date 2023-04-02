package com.gourav.competrace.app_core.util

import android.annotation.SuppressLint
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentTimeInMillis() = Date().time

fun minutesToMillis(minutes: Int) = minutes * 60 * 1000L

@SuppressLint("SimpleDateFormat")
fun getTodaysDate(): String = SimpleDateFormat("EEE, d-MMM").format(Date())

private fun convertMillisToDHMS(timeInMilliSec: Long): Array<Long> {
    val (MsID, MsIH, MsIM, MsIS) = listOf(86400000, 3600000, 60000, 1000)
    var millis = timeInMilliSec
    val days = millis / MsID
    millis %= MsID
    val h = millis / MsIH
    millis %= MsIH
    val m = millis / MsIM
    millis %= MsIM
    val s = millis / MsIS
    return arrayOf(days, h, m, s)
}

fun getFormattedTime(timeInMillis: Long, format: String = "%02d:%02d:%02d"): AnnotatedString {
    return buildAnnotatedString {
        val (days, h, m, s) = convertMillisToDHMS(timeInMillis)
        val startPhrase = if (h + m + s != 0L) when (days) {
            0L -> ""
            1L -> "$days day, "
            else -> "$days days, "
        } else when (days) {
            1L -> "$days day"
            else -> "$days days"
        }
        append(startPhrase)
        if (h + m + s != 0L) {
            val timeStamp = String.format(format, h, m, s)
            withStyle(style = SpanStyle(fontFeatureSettings = "tnum")) {
                append(timeStamp)
            }
            append(" hrs")
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun unixToDMYETZ(timeStampInMillis: Long): String {
    val format = SimpleDateFormat("d-MMM-yyyy, EEEE hh:mm a z")
    return format.format(Date(timeStampInMillis))
}

@SuppressLint("SimpleDateFormat")
fun unixToDMET(timeStampInMillis: Long): AnnotatedString {
    val date = Date(timeStampInMillis)
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.SemiBold)
        ){
            append(SimpleDateFormat("d-MMM").format(date))
        }
        append(SimpleDateFormat(", EEEE ").format(date))
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.SemiBold)
        ){
            append(SimpleDateFormat("hh:mm a").format(date))
        }
    }
}


@SuppressLint("SimpleDateFormat")
fun unixToTime(timeStampInMillis: Long): String {
    val format = SimpleDateFormat("hh:mm a")
    return format.format(Date(timeStampInMillis))
}

@SuppressLint("SimpleDateFormat")
fun unixToDMYE(timeStampInMillis: Long): String {
    val format = SimpleDateFormat("d-MMM-yyyy, EEE")
    return format.format(Date(timeStampInMillis))
}


@SuppressLint("SimpleDateFormat")
fun formattedStringToUnix(value: String): Long {
    val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").apply { timeZone = TimeZone.getTimeZone("UTC") }
    val format2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").apply { timeZone = TimeZone.getTimeZone("UTC") }

    return when {
        format1.isMatched(value) -> format1.parse(value)?.time ?: 0
        format2.isMatched(value) -> format2.parse(value)?.time ?: 0
        else -> 0
    }
}

private fun SimpleDateFormat.isMatched(value: String): Boolean {
    try {
        parse(value)
    } catch (_: Exception) {
        return false
    }
    return true
}



