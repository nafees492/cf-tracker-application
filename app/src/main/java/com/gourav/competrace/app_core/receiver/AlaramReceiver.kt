package com.gourav.competrace.app_core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gourav.competrace.app_core.AlarmScheduler
import com.gourav.competrace.app_core.AndroidNotificationScheduler
import com.gourav.competrace.app_core.NotificationItem
import com.gourav.competrace.app_core.NotificationScheduler

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { handleAlarmData(context, it) }
    }

    private fun handleAlarmData(context: Context?, intent: Intent) {

        context?.let {
            val title = intent.getStringExtra(AlarmScheduler.TITLE) ?: ""
            val description = intent.getStringExtra(AlarmScheduler.MESSAGE) ?: ""

            val notificationScheduler = AndroidNotificationScheduler(context = it)

            NotificationItem(
                channelId = NotificationScheduler.CONTEST_CHANNEL_ID,
                title = title,
                description = description,
                priority = NotificationCompat.PRIORITY_HIGH
            ).let(notificationScheduler::fireNotification)
        }
    }
}