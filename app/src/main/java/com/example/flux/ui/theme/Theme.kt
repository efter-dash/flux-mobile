package com.example.flux.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode(val label: String) {
    SYSTEM("System Default"),
    DARK("Dark Mode"),
    LIGHT("Light Mode")
}

private val DarkColorScheme = darkColorScheme(
    primary = FluxAccent,
    onPrimary = BaseCanvas,
    primaryContainer = RaisedCard,
    onPrimaryContainer = InkLight,
    secondary = FluxSecondary,
    onSecondary = BaseCanvas,
    background = BaseCanvas,
    onBackground = InkLight,
    surface = PanelCard,
    onSurface = InkLight,
    surfaceVariant = RaisedCard,
    onSurfaceVariant = InkDim,
    outline = LineBorder,
    error = FluxDanger,
    onError = BaseCanvas
)

private val LightColorScheme = lightColorScheme(
    primary = LightFluxAccent,
    onPrimary = LightPanelCard,
    primaryContainer = LightRaisedCard,
    onPrimaryContainer = LightInk,
    secondary = LightFluxSecondary,
    onSecondary = LightPanelCard,
    background = LightBaseCanvas,
    onBackground = LightInk,
    surface = LightPanelCard,
    onSurface = LightInk,
    surfaceVariant = LightRaisedCard,
    onSurfaceVariant = LightInkDim,
    outline = LightLineBorder,
    error = LightFluxDanger,
    onError = LightPanelCard
)

@Composable
fun FluxTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FluxTypography,
        content = content
    )
}
