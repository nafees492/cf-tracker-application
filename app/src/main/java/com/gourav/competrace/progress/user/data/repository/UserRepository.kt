package com.gourav.competrace.progress.user.data.repository

import android.content.Context
import com.gourav.competrace.progress.user.data.database.UserDatabase
import com.gourav.competrace.progress.user.model.CompetraceUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository(context: Context) {

    private val dao = UserDatabase.getInstance(context)?.userDao()!!

    fun getAllUsers(): Flow<List<CompetraceUser>> = dao.getAllUsers()

    suspend fun addUser(user: CompetraceUser){
        dao.addUser(user)
    }

    suspend fun deleteUser(user: CompetraceUser){
        dao.deleteUser(user)
    }

    suspend fun deleteAllUsers(){
        dao.deleteAllUsers()
    }
}