package com.gourav.competrace.contests.data.repository

import android.content.Context
import com.gourav.competrace.contests.data.dao.AlarmDao
import com.gourav.competrace.contests.data.database.AlarmDatabase
import com.gourav.competrace.contests.model.ContestAlarmItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AlarmRepository(context: Context) {
    private val dao: AlarmDao = AlarmDatabase.getInstance(context)?.alarmDao()!!

    fun getAllAlarms(): Flow<List<ContestAlarmItem>> = flow {
        emit(dao.gelAllAlarms())
    }.flowOn(Dispatchers.IO)

    suspend fun addAlarm(item: ContestAlarmItem) {
        dao.addAlarm(item)
    }

    suspend fun updateAlarm(item: ContestAlarmItem) {
        dao.updateAlarm(item)
    }

    suspend fun deleteAlarm(item: ContestAlarmItem) {
        dao.deleteAlarm(item)
    }

    suspend fun deleteALlAlarms(){
        dao.deleteAllAlarms()
    }
}