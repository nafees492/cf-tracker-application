package com.theruralguys.competrace.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

fun getTodaysDate() = Date()

@SuppressLint("DefaultLocale")
fun convertMillisToDHMS(timeInMilliSec: Long): Array<Long> {
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

fun formatLength(days: Long, h: Long, m: Long, s: Long) =
    if (h + m + s != 0L) when (days) {
        0L -> String.format("%02d:%02d:%02d", h, m, s) + " hrs"
        1L -> "$days day and " + String.format("%02d:%02d:%02d", h, m, s) + " hrs"
        else -> "$days days and " + String.format("%02d:%02d:%02d", h, m, s) + " hrs"
    } else when (days) {
        1L -> "$days day"
        else -> "$days days"
    }


@SuppressLint("SimpleDateFormat")
fun unixToDateAndTime(timeStampInMillis: Long): String {
    val format = SimpleDateFormat("d MMMM yyyy, EEEE hh:mm a z")
    return format.format(Date(timeStampInMillis))
}

@SuppressLint("SimpleDateFormat")
fun unixToDateDayTime(timeStampInMillis: Long): String {
    val format = SimpleDateFormat("d-MMM, EEEE hh:mm a")
    return format.format(Date(timeStampInMillis))
}

@SuppressLint("SimpleDateFormat")
fun unixToTime(timeStampInMillis: Long): String {
    val format = SimpleDateFormat("hh:mm a z")
    return format.format(Date(timeStampInMillis))
}