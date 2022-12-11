package com.example.cfprogresstracker.ui.theme

import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val CFPTDarkColorScheme = darkColorScheme()

private val CFPTLightColorScheme = lightColorScheme()

@Composable
fun CodeforcesProgressTrackerTheme(
    currentTheme: String = AppTheme.SystemDefault.name,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val myColorScheme = when(currentTheme){
        AppTheme.Dark.name -> {
            CFPTDarkColorScheme
        }
        AppTheme.Light.name -> {
            CFPTLightColorScheme
        }
        AppTheme.Dynamic.name -> {
            if (isDarkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        else -> {
            if (isDarkTheme) CFPTDarkColorScheme
            else CFPTLightColorScheme
        }
    }

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = myColorScheme.surface,
            darkIcons = !isDarkTheme
        )
    }

    MaterialTheme(
        colorScheme = myColorScheme,
        typography = CFPTTypography
    ) {
        // TODO (M3): MaterialTheme doesn't provide LocalIndication, remove when it does
        val rippleIndication = rememberRipple()
        CompositionLocalProvider(
            LocalIndication provides rippleIndication,
            content = content
        )
    }

}