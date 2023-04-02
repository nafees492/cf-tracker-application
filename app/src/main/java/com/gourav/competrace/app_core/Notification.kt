package com.gourav.competrace.app_core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gourav.competrace.R
import com.gourav.competrace.app_core.ui.MainActivity

data class NotificationItem(
    val channelId: String,
    val title: String,
    val description: String,
    val priority: Int,
    val largeIconId: Int? = null
)

interface Notification {
    fun fireNotification(
        item: NotificationItem,
        vararg action: NotificationCompat.Action?
    )

    fun cancelNotification(item: NotificationItem)
    fun updateNotification(item: NotificationItem)

    companion object {
        const val CONTEST_CHANNEL_ID = "contest-channel-id"
    }
}

class AndroidNotification(private val context: Context) : Notification {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                Notification.CONTEST_CHANNEL_ID,
                context.getString(R.string.contest_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    override fun fireNotification(
        item: NotificationItem,
        vararg action: NotificationCompat.Action?
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, item.channelId).apply {
            priority = item.priority

            setSmallIcon(R.drawable.competrace_96)
            setContentTitle(item.title)
            setContentText(item.description)
            setStyle(NotificationCompat.BigTextStyle().bigText(item.description))
            setContentIntent(pendingIntent)
            setAutoCancel(true)

            action.forEach {action -> action?.let(this::addAction) }

            item.largeIconId?.let {
                setLargeIcon(BitmapFactory.decodeResource(context.resources, it))
            }
        }


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    override fun cancelNotification(item: NotificationItem) {
        TODO("Not yet implemented")
    }

    override fun updateNotification(item: NotificationItem) {
        TODO("Not yet implemented")
    }
}