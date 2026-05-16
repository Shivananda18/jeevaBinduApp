package com.jeevabindu.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = RedAlert,
    secondary = GreenAvailable,
    tertiary = RedAlert.copy(alpha = 0.7f),
    background = BackgroundLight,
    surface = BackgroundLight
)

private val DarkScheme = darkColorScheme(
    primary = RedAlert,
    secondary = GreenAvailable,
    tertiary = RedAlert.copy(alpha = 0.8f),
    background = BackgroundDark,
    surface = SurfaceDark
)

@Composable
fun JeevaBinduTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content = content
    )
}
