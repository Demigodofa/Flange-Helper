package com.kevin.flangejointassembly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = FlangeBlueLight,
    onPrimary = FlangeBlue,
    secondary = FlangeBlueLight,
    background = FlangeBlue,
    surface = FlangeBlue,
    onBackground = FlangeBlueLight,
    onSurface = FlangeBlueLight
)

private val LightColorScheme = lightColorScheme(
    primary = FlangeBlue,
    onPrimary = FlangeBlueLight,
    secondary = FlangeBlue,
    background = FlangeBackground,
    surface = FlangeSurface,
    onBackground = FlangeOnSurface,
    onSurface = FlangeOnSurface
)

@Composable
fun FlangeJointAssemblyHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
