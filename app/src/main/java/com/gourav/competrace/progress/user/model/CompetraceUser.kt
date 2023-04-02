package com.gourav.competrace.progress.user.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class CompetraceUser(
    @PrimaryKey(autoGenerate = false) val handle: String,
)
