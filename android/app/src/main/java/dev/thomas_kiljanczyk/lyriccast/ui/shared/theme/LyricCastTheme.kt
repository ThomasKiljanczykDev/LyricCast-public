/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 12:07 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 12:07 AM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ThemeOption

// Light theme colors for Android 11 and below
private val lightScheme = lightColorScheme(
    primary = Color(0xFF00668B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0F3FF),
    onPrimaryContainer = Color(0xFF001F2B),

    secondary = Color(0xFF4E616C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE3E9),
    onSecondaryContainer = Color(0xFF0B1D27),

    tertiary = Color(0xFF657985),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0F3FF),
    onTertiaryContainer = Color(0xFF1F333F),

    background = Color(0xFFF9FCFF),
    onBackground = Color(0xFF191C1E),

    surface = Color(0xFFF9FCFF),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDCE3E9),
    onSurfaceVariant = Color(0xFF40484D),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF70787E),
    outlineVariant = Color(0xFFC0C8CE),
    scrim = Color(0xFF000000)
)

// Dark theme colors for Android 11 and below
private val darkScheme = darkColorScheme(
    primary = Color(0xFF76D1FF),
    onPrimary = Color(0xFF003549),
    primaryContainer = Color(0xFF004C69),
    onPrimaryContainer = Color(0xFFC1E8FF),

    secondary = Color(0xFFB5CAD7),
    onSecondary = Color(0xFF20333D),
    secondaryContainer = Color(0xFF374954),
    onSecondaryContainer = Color(0xFFD1E6F3),

    tertiary = Color(0xFFB5CAD7),
    onTertiary = Color(0xFF344854),
    tertiaryContainer = Color(0xFF4C616C),
    onTertiaryContainer = Color(0xFFD1E6F3),

    background = Color(0xFF191C1E),
    onBackground = Color(0xFFE1E3E5),

    surface = Color(0xFF191C1E),
    onSurface = Color(0xFFE1E3E5),
    surfaceVariant = Color(0xFF40484D),
    onSurfaceVariant = Color(0xFFC0C8CE),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF40484D),
    scrim = Color(0xFF000000)
)

/**
 * Converts ThemeOption to boolean for dark theme usage
 */
@Composable
private fun ThemeOption?.shouldUseDarkTheme(): Boolean = when (this) {
    ThemeOption.LIGHT -> false
    ThemeOption.DARK -> true
    ThemeOption.SYSTEM, null -> isSystemInDarkTheme()
}

/**
 * Checks if dynamic colors are supported on the current device
 * Dynamic colors are available on Android 12 (API 31) and above
 */
fun isDynamicColorSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LyricCastTheme(
    themeOption: ThemeOption? = null,
    useDarkTheme: Boolean = themeOption?.shouldUseDarkTheme() ?: isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        isDynamicColorSupported() -> {
            val context = LocalContext.current

            if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        useDarkTheme -> darkScheme

        else -> lightScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colors,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
