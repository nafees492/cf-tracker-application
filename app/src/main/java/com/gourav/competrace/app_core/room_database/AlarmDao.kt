package com.gourav.competrace.app_core.room_database

import androidx.room.*
import com.gourav.competrace.app_core.AlarmItem

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarmItem: AlarmItem)

    @Update
    suspend fun updateAlarm(alarmItem: AlarmItem)

    @Delete
    suspend fun deleteAlarm(alarmItem: AlarmItem)

    @Query("Select * from scheduled_alarms")
    suspend fun gelAllAlarms(): List<AlarmItem>

    @Query("DELETE FROM scheduled_alarms")
    suspend fun deleteAllAlarms()
}