package com.akillifinans.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary          = Primary,
    onPrimary        = OnPrimary,
    primaryContainer = PrimaryContainer,

    secondary        = Secondary,
    onSecondary      = Color.White,

    background       = Background,
    onBackground     = OnBackground,

    surface          = Surface,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVar,
    onSurfaceVariant = OnSurfaceVar,

    outline          = OutlineVar,
    outlineVariant   = OutlineVar,

    error            = LossRed,
    onError          = Color.White,
)

@Composable
fun AkilliFinansTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Uygulama yalnızca dark theme kullanır; finans uygulamalarında dark mode standarttır.
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = AkilliFinansTypography,
        content     = content
    )
}
