package com.gourav.competrace.app_core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CompetraceFMS: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val remoteNotif = message.notification
        Log.d(TAG, "Remote Notification: ${remoteNotif?.title}")

        remoteNotif?.let {
            val notificationItem = NotificationItem(
                channelId = Notification.FIREBASE_CHANNEL_ID,
                title = it.title.toString(),
                description = it.body.toString()
            )

            AndroidNotification(applicationContext).fireNotification(
                notificationItem
            )
        }
    }

    companion object{
        private const val TAG = "Competrace FCM"
    }
}