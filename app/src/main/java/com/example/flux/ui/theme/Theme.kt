package com.example.flux.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

@Composable
fun FluxTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BaseCanvas.toArgb()
                window.navigationBarColor = BaseCanvas.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = FluxTypography,
        content = content
    )
}
