package com.gourav.competrace.app_core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gourav.competrace.app_core.receiver.AlarmReceiver

@Entity(tableName = "scheduled_alarms")
data class AlarmItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contestId: String,
    val timeInMillis: Long,
    val title: String,
    val message: String
)

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)

    companion object {
        const val TITLE = "title"
        const val MESSAGE = "message"
    }
}

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmScheduler.TITLE, item.title)
            putExtra(AlarmScheduler.MESSAGE, item.message)
        }

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
    }

    override fun cancel(item: AlarmItem) {
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