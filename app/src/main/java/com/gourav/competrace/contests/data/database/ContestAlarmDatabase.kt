package com.gourav.competrace.contests.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gourav.competrace.contests.data.dao.ContestAlarmDao
import com.gourav.competrace.contests.model.ContestAlarmItem

@Database(entities = [ContestAlarmItem::class], version = 1, exportSchema = false)
abstract class ContestAlarmDatabase: RoomDatabase() {
    abstract fun alarmDao() : ContestAlarmDao

    companion object {
        private var INSTANCE: ContestAlarmDatabase? = null
        private const val DB_NAME = "contest_alarm_database"

        fun getInstance(context: Context): ContestAlarmDatabase? {
            if (INSTANCE == null) {
                synchronized(ContestAlarmDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContestAlarmDatabase::class.java,
                        DB_NAME
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}