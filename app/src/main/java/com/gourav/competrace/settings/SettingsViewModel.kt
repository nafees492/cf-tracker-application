package com.gourav.competrace.settings

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.competrace.BuildConfig
import com.gourav.competrace.R
import com.gourav.competrace.app_core.data.UserPreferences
import com.gourav.competrace.app_core.ui.theme.CompetraceThemeNames
import com.gourav.competrace.app_core.ui.theme.DarkModePref
import com.gourav.competrace.app_core.util.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val isDeviceAbove12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val appVersionName = BuildConfig.VERSION_NAME

    val currentTheme = userPreferences.currentThemeFlow
    val darkModePref = userPreferences.darkModePrefFlow
    val showTags = userPreferences.showTagsFlow

    val scheduleNotifBefore = userPreferences.scheduleNotifBeforeFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        60
    )

    fun changeScheduleNotifBeforeTo(value: Int){
        viewModelScope.launch {
            userPreferences.setScheduleNotifBefore(value)
        }
    }

    val themeOptions = arrayListOf(CompetraceThemeNames.DEFAULT).apply {
        if (isDeviceAbove12) add(CompetraceThemeNames.DYNAMIC)
    }

    val darkModePrefOptions =
        listOf(DarkModePref.SYSTEM_DEFAULT, DarkModePref.LIGHT, DarkModePref.DARK)

    fun setTheme(theme: String){
        viewModelScope.launch {
            userPreferences.setCurrentTheme(theme)
        }
    }

    fun setDarkModePref(pref: String){
        viewModelScope.launch {
            userPreferences.setDarkModePref(pref)
        }
    }

    fun setShowTags(value: Boolean){
        viewModelScope.launch {
            userPreferences.setShowTags(value)
        }
    }

    companion object {
        private val TAG = "Settings View Model"
    }
}