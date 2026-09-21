package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val UnplugColorScheme = darkColorScheme(
    primary = ElectricChartreuse,
    onPrimary = ObsidianBlack,
    primaryContainer = SurfaceDark,
    onPrimaryContainer = ChalkWhite,
    secondary = NeutralGray,
    onSecondary = ChalkWhite,
    secondaryContainer = SurfaceDarkHover,
    onSecondaryContainer = ChalkWhite,
    tertiary = LightNeutral,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = ChalkWhite,
    surface = SurfaceDark,
    onSurface = ChalkWhite,
    surfaceVariant = SurfaceDarkHover,
    onSurfaceVariant = NeutralGray,
    outline = SurfaceBorder,
    outlineVariant = SurfaceDarkHover,
    error = AlertRed,
    onError = ChalkWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UnplugColorScheme,
        typography = Typography,
        content = content
    )
}

