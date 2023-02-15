package com.gourav.competrace.app_core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.gourav.competrace.R
import com.gourav.competrace.app_core.MainActivity

class SampleNotification (var context: Context, var title: String, var message: String) {

    val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: NotificationCompat.Builder

    fun fireNotification(){

        val notificationID = 100

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)

        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .addAction(R.drawable.ic_round_insights_24, "Open Message", pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    companion object {
        const val TAG = "Sample Notification"
        const val CHANNEL_ID = "id1"
        const val CHANNEL_NAME = "channel1"
    }}