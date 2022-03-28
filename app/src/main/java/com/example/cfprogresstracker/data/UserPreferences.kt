package com.example.cfprogresstracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"
        private val Context.dataStore by preferencesDataStore(
            name = USER_PREFERENCES_NAME
        )
        private val HANDLE_NAME = stringPreferencesKey("user_name")
    }

    val handleNameFlow: Flow<String?> = appContext.dataStore.data.map { preferences ->
        preferences[HANDLE_NAME]
    }

    suspend fun setHandleName(handleName: String) {
        appContext.dataStore.edit { preferences ->
            preferences[HANDLE_NAME] = handleName
        }
    }
}