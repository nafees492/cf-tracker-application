package com.gourav.competrace.app_core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
    val priority: Int
)

interface NotificationScheduler {
    fun fireNotification(item: NotificationItem)
    fun cancelNotification(item: NotificationItem)
    fun updateNotification(item: NotificationItem)

    companion object {
        const val CONTEST_CHANNEL_ID = "contest-channel-id"
    }
}

class AndroidNotificationScheduler(private val context: Context) : NotificationScheduler {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                NotificationScheduler.CONTEST_CHANNEL_ID,
                context.getString(R.string.contest_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }

    override fun fireNotification(item: NotificationItem) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, item.channelId)
            .setSmallIcon(R.drawable.competrace_96)
            .setContentTitle(item.title)
            .setContentText(item.description)
            .setPriority(item.priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (
                ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
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