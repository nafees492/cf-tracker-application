package com.gourav.competrace.app_core.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gourav.competrace.app_core.ui.theme.CompetraceThemeNames
import com.gourav.competrace.app_core.ui.theme.DarkModePref
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
        private val IS_USER_LOGGED_IN = stringPreferencesKey("is_user_logged_in")
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
        private val SHOW_PLATFORMS = booleanPreferencesKey("show_platforms")
        private val SELECTED_CONTEST_SITE_INDEX = intPreferencesKey("selected_contest_site_index")
    }

    val handleNameFlow: Flow<String> = appContext.handleDataStore.data.map {
        it[HANDLE_NAME] ?: ""
    }

    suspend fun setHandleName(handleName: String) {
        appContext.handleDataStore.edit {
            it[HANDLE_NAME] = handleName
        }
    }

    val currentThemeFlow: Flow<String> = appContext.appThemeDataStore.data.map {
        it[CURRENT_THEME] ?: CompetraceThemeNames.DEFAULT
    }

    suspend fun setCurrentTheme(theme: String) {
        appContext.appThemeDataStore.edit {
            it[CURRENT_THEME] = theme
        }
    }

    val darkModePrefFlow: Flow<String> = appContext.appThemeDataStore.data.map {
        it[DARK_MODE_PREF] ?: DarkModePref.SYSTEM_DEFAULT
    }

    suspend fun setDarkModePref(pref: String){
        appContext.appThemeDataStore.edit {
            it[DARK_MODE_PREF] = pref
        }
    }

    val showTagsFlow: Flow<Boolean> = appContext.generalDataStore.data.map {
        it[SHOW_TAGS] ?: true
    }

    suspend fun setShowTags(value: Boolean){
        appContext.generalDataStore.edit {
            it[SHOW_TAGS] = value
        }
    }

    /*val showPlatformFlow: Flow<Boolean> = appContext.generalDataStore.data.map {
        it[SHOW_PLATFORMS] ?: true
    }

    suspend fun setShowPlatform(value: Boolean){
        appContext.generalDataStore.edit {
            it[SHOW_PLATFORMS] = value
        }
    }*/

    val selectedContestSiteIndexFlow: Flow<Int> = appContext.generalDataStore.data.map {
        it[SELECTED_CONTEST_SITE_INDEX] ?: 0
    }

    suspend fun setSelectedContestSiteIndex(value: Int){
        appContext.generalDataStore.edit {
            it[SELECTED_CONTEST_SITE_INDEX] = value
        }
    }
}