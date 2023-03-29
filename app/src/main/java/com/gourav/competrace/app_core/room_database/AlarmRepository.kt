package com.gourav.competrace.app_core.room_database

import android.content.Context
import com.gourav.competrace.app_core.AlarmItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AlarmRepository(context: Context) {
    private val dao: AlarmDao = AlarmDatabase.getInstance(context)?.alarmDao()!!

    fun getAllAlarms(): Flow<List<AlarmItem>> = flow {
        emit(dao.gelAllAlarms())
    }.flowOn(Dispatchers.IO)

    suspend fun addAlarm(item: AlarmItem) {
        dao.addAlarm(item)
    }

    suspend fun updateAlarm(item: AlarmItem) {
        dao.updateAlarm(item)
    }

    suspend fun deleteAlarm(item: AlarmItem) {
        dao.deleteAlarm(item)
    }

    suspend fun deleteALlAlarms(){
        dao.deleteAllAlarms()
    }
}