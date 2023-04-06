package com.gourav.competrace.contests.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.gourav.competrace.app_core.receiver.AlarmReceiver
import com.gourav.competrace.app_core.util.TimeUtils
import com.gourav.competrace.contests.model.ContestAlarmItem

interface ContestAlarmScheduler {
    fun schedule(item: ContestAlarmItem)
    fun cancel(item: ContestAlarmItem)

    companion object {
        const val TITLE = "title"
        const val MESSAGE = "message"
        const val REGISTRATION_URL = "registration_url"
    }
}

class ContestContestAlarmSchedulerImpl(
    private val context: Context
) : ContestAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private val canScheduleExactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else true

    override fun schedule(item: ContestAlarmItem) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ContestAlarmScheduler.TITLE, item.title)
            putExtra(ContestAlarmScheduler.MESSAGE, item.message)
            putExtra(ContestAlarmScheduler.REGISTRATION_URL, item.registrationUrl)
        }

        if (canScheduleExactAlarm) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    item.id,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                item.timeInMillis,
                TimeUtils.minutesToMillis(10),
                PendingIntent.getBroadcast(
                    context,
                    item.id,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(item: ContestAlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}