package com.example.competrace.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.competrace.ui.theme.DarkModePref
import com.example.competrace.ui.theme.MyTheme
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
        private val CURRENT_THEME = stringPreferencesKey("current_theme")
        private val DARK_MODE_PREF = stringPreferencesKey("dark_mode_pref")
    }

    val handleNameFlow: Flow<String?> = appContext.dataStore.data.map { preferences ->
        preferences[HANDLE_NAME]
    }

    suspend fun setHandleName(handleName: String) {
        appContext.dataStore.edit { preferences ->
            preferences[HANDLE_NAME] = handleName
        }
    }

    val currentThemeFlow: Flow<String?> = appContext.dataStore.data.map { preferences ->
        preferences[CURRENT_THEME] ?: MyTheme.DEFAULT
    }

    suspend fun setCurrentTheme(theme: String) {
        appContext.dataStore.edit { preferences ->
            preferences[CURRENT_THEME] = theme
        }
    }

    val darkModePrefFlow: Flow<String?> = appContext.dataStore.data.map { preferences ->
        preferences[DARK_MODE_PREF] ?: DarkModePref.SYSTEM_DEFAULT
    }

    suspend fun setDarkModePref(pref: String){
        appContext.dataStore.edit { preferences ->
            preferences[DARK_MODE_PREF] = pref
        }
    }

}