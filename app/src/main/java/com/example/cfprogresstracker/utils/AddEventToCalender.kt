package com.example.cfprogresstracker.utils

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract

fun addEventToCalendar (context: Context, title: String, startTime: Long, endTime: Long, location: String, description: String) {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, title) // Simple title
        putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime) // Only date part is considered when ALL_DAY is true; Same as DTSTART
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime) // Only date part is considered when ALL_DAY is true
        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        putExtra(CalendarContract.Events.DESCRIPTION, description) // Description
        putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
        putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE)
    }
    //.putExtra(Intent.EXTRA_EMAIL, "fooInviteeOne@gmail.com,fooInviteeTwo@gmail.com")
    //.putExtra(CalendarContract.Events.RRULE, getRRule()) // Recurrence rule

    context.startActivity(intent)
}