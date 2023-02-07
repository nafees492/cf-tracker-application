package com.gourav.competrace.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gourav.competrace.ui.theme.CompetraceTheme
import com.gourav.competrace.ui.theme.DarkModePref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    companion object {
        private const val USER_HANDLE_PREFERENCES_NAME = "competrace_user_handle_preferences"
        private val Context.handleDataStore by preferencesDataStore(
            name = USER_HANDLE_PREFERENCES_NAME
        )
        private val HANDLE_NAME = stringPreferencesKey("user_name")


        private const val APP_THEME_PREFERENCES_NAME = "competrace_app_theme_preferences"
        private val Context.appThemeDataStore by preferencesDataStore(
            name = APP_THEME_PREFERENCES_NAME
        )
        private val CURRENT_THEME = stringPreferencesKey("current_theme")
        private val DARK_MODE_PREF = stringPreferencesKey("dark_mode_pref")

        private const val GENERAL_PREFERENCES_NAME = "competrace_general_preferences"
        private val Context.generalDataStore by preferencesDataStore(
            name = GENERAL_PREFERENCES_NAME
        )
        private val SHOW_TAGS = booleanPreferencesKey("show_tags")
    }

    val handleNameFlow: Flow<String?> = appContext.handleDataStore.data.map { preferences ->
        preferences[HANDLE_NAME]
    }

    suspend fun setHandleName(handleName: String) {
        appContext.handleDataStore.edit { preferences ->
            preferences[HANDLE_NAME] = handleName
        }
    }

    val currentThemeFlow: Flow<String> = appContext.appThemeDataStore.data.map { preferences ->
        preferences[CURRENT_THEME] ?: CompetraceTheme.DEFAULT
    }

    suspend fun setCurrentTheme(theme: String) {
        appContext.appThemeDataStore.edit { preferences ->
            preferences[CURRENT_THEME] = theme
        }
    }

    val darkModePrefFlow: Flow<String> = appContext.appThemeDataStore.data.map { preferences ->
        preferences[DARK_MODE_PREF] ?: DarkModePref.SYSTEM_DEFAULT
    }

    suspend fun setDarkModePref(pref: String){
        appContext.appThemeDataStore.edit { preferences ->
            preferences[DARK_MODE_PREF] = pref
        }
    }

    val showTagsFlow: Flow<Boolean> = appContext.generalDataStore.data.map { preferences ->
        preferences[SHOW_TAGS] ?: true
    }

    suspend fun setShowTagsFlow(value: Boolean){
        appContext.generalDataStore.edit { preferences ->
            preferences[SHOW_TAGS] = value
        }
    }
}