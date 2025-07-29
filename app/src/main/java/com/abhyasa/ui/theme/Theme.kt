package com.abhyasa.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = CustomBlue,
    onPrimary = CustomOnPrimary,
    secondary = CustomSecondary,
    onSecondary = CustomOnSecondary,
    background = CustomBackground,
    onBackground = CustomOnBackground,
    surface = CustomSurface,
    onSurface = CustomOnSurface,
    tertiary = CustomBlueLight
)

@Composable
fun ExamPractiseHelperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}