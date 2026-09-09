package org.sightguide.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = HighContrastDarkPrimary,
    onPrimary = HighContrastDarkOnPrimary,
    secondary = HighContrastDarkSecondary,
    onSecondary = HighContrastDarkOnSecondary,
    background = HighContrastDarkBackground,
    onBackground = HighContrastDarkTextPrimary,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkTextPrimary,
    surfaceVariant = HighContrastDarkSurfaceVariant,
    onSurfaceVariant = HighContrastDarkTextSecondary,
    error = HighContrastDarkCritical,
    onError = HighContrastDarkOnCritical
)

private val LightColorScheme = lightColorScheme(
    primary = HighContrastLightPrimary,
    onPrimary = HighContrastLightOnPrimary,
    secondary = HighContrastLightSecondary,
    onSecondary = HighContrastLightOnSecondary,
    background = HighContrastLightBackground,
    onBackground = HighContrastLightTextPrimary,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightTextPrimary,
    surfaceVariant = HighContrastLightSurfaceVariant,
    onSurfaceVariant = HighContrastLightTextSecondary,
    error = HighContrastLightCritical,
    onError = HighContrastLightOnCritical
)

@Composable
fun SightGuideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SightGuideTypography,
        content = content
    )
}
