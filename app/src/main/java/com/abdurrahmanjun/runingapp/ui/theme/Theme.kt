package com.abdurrahmanjun.runingapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MomentumColorScheme = lightColorScheme(
    primary = MomentumColors.Teal,
    onPrimary = MomentumColors.Card,
    secondary = MomentumColors.Mint,
    onSecondary = MomentumColors.Ink,
    tertiary = MomentumColors.Lime,
    onTertiary = MomentumColors.Ink,
    background = MomentumColors.Paper,
    onBackground = MomentumColors.Ink,
    surface = MomentumColors.Card,
    onSurface = MomentumColors.Ink,
    surfaceVariant = MomentumColors.Paper,
    onSurfaceVariant = MomentumColors.Muted,
    outline = MomentumColors.Line,
    error = MomentumColors.Destructive,
)

/**
 * Root theme applied by every ComposeView host. Wraps Material3 with the
 * Momentum colour scheme + type scale; screens draw their own dark surfaces
 * (e.g. Live Run) locally where the design calls for them.
 */
@Composable
fun MomentumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MomentumColorScheme,
        typography = MomentumTypography,
        content = content,
    )
}
