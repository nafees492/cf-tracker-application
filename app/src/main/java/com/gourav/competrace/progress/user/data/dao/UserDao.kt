package com.gourav.competrace.progress.user.data.dao

import androidx.room.*
import com.gourav.competrace.progress.user.model.CompetraceUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: CompetraceUser)

    @Delete
    suspend fun deleteUser(user: CompetraceUser)

    @Query("Select * from user_table")
    fun getAllUsers(): Flow<List<CompetraceUser>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()
}