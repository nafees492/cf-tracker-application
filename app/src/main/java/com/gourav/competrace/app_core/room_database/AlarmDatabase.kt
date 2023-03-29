package com.gourav.competrace.app_core.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gourav.competrace.app_core.AlarmItem

@Database(entities = [AlarmItem::class], version = 1, exportSchema = false)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun alarmDao() : AlarmDao

    companion object {
        private var INSTANCE: AlarmDatabase? = null
        private const val DB_NAME = "alarm_database"

        fun getInstance(context: Context): AlarmDatabase? {
            if (INSTANCE == null) {
                synchronized(AlarmDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmDatabase::class.java,
                        DB_NAME
                    ).allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}