package com.gourav.competrace.app_core.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.gourav.competrace.R
import com.gourav.competrace.contests.data.ContestAlarmScheduler
import com.gourav.competrace.app_core.AndroidNotification
import com.gourav.competrace.app_core.NotificationItem
import com.gourav.competrace.app_core.Notification
import com.gourav.competrace.app_core.util.Sites

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { handleAlarmData(context, it) }
    }

    private fun handleAlarmData(context: Context?, intent: Intent) {

        context?.let {
            val title = intent.getStringExtra(ContestAlarmScheduler.TITLE) ?: ""
            val description = intent.getStringExtra(ContestAlarmScheduler.MESSAGE) ?: ""
            val registrationUrl = intent.getStringExtra(ContestAlarmScheduler.REGISTRATION_URL) ?: ""

            val registrationAction = if(registrationUrl.isNotBlank()){
                NotificationCompat.Action(
                    R.drawable.ic_priority_high_24px,
                    context.getString(R.string.register_now),
                    PendingIntent.getActivity(
                        context,
                        0,
                        Intent(Intent.ACTION_VIEW, Uri.parse(registrationUrl)),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            } else null

            val site = Sites.getSite(title)

            AndroidNotification(context = it).fireNotification(
                NotificationItem(
                    channelId = Notification.CONTEST_CHANNEL_ID,
                    title = title,
                    description = description,
                    largeIconId = site.iconId,
                    priority = NotificationCompat.PRIORITY_HIGH
                ),
                registrationAction
            )
        }
    }

}