package com.gourav.competrace.contests.data.dao

import androidx.room.*
import com.gourav.competrace.contests.model.ContestAlarmItem

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAlarm(alarmItem: ContestAlarmItem)

    @Update
    suspend fun updateAlarm(alarmItem: ContestAlarmItem)

    @Delete
    suspend fun deleteAlarm(alarmItem: ContestAlarmItem)

    @Query("Select * from scheduled_alarms")
    suspend fun gelAllAlarms(): List<ContestAlarmItem>

    @Query("DELETE FROM scheduled_alarms")
    suspend fun deleteAllAlarms()
}