package com.example.cfprogresstracker.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val CFPTDarkColorScheme = darkColorScheme(
    primary = Blue30,
    onPrimary = Blue95,
    primaryContainer = Blue60,
    onPrimaryContainer = Blue90,
    inversePrimary = Blue90,
    secondary = Green40,
    onSecondary = Green95,
    secondaryContainer = Green50,
    onSecondaryContainer = Green95,
    tertiary = Yellow80,
    onTertiary = Yellow20,
    tertiaryContainer = Yellow30,
    onTertiaryContainer = Yellow80,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Blue5,
    onBackground = Blue95,
    surface = Blue10,
    onSurface = Blue95,
    inverseSurface = Grey90,
    inverseOnSurface = Grey20,
    surfaceVariant = Grey20,
    onSurfaceVariant = Grey80,
    outline = Grey90
)

private val CFPTLightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Blue95,
    primaryContainer = Blue80,
    onPrimaryContainer = Blue10,
    inversePrimary = Blue80,
    secondary = Green30,
    onSecondary = Green95,
    secondaryContainer = Green80,
    onSecondaryContainer = Green10,
    tertiary = Yellow40,
    onTertiary = Color.Black,
    tertiaryContainer = Yellow90,
    onTertiaryContainer = Yellow20,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Blue95,
    onBackground = Blue10,
    surface = Blue90,
    onSurface = Blue10,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = Grey90,
    onSurfaceVariant = Grey10,
    outline = Grey20
)


@Composable
fun CodeforcesProgressTrackerTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val myColorScheme = when {
        isDarkTheme -> CFPTDarkColorScheme
        else -> CFPTLightColorScheme
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