package com.gourav.competrace.contests.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_alarms")
data class ContestAlarmItem(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val contestId: String,
    val timeInMillis: Long,
    val title: String,
    val message: String,
    val registrationUrl: String
)
