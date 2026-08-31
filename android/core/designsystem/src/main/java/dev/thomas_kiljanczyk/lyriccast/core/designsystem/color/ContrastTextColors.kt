package dev.thomas_kiljanczyk.lyriccast.core.designsystem.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LocalUseDarkTheme
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.isDynamicColorSupported

/**
 * Text tones a caller picks between by contrast when drawing on a user-chosen colour, such as a
 * category pill.
 *
 * Follows [LocalUseDarkTheme], not the device configuration: the app's theme setting can override
 * the system one, and a resource qualifier would miss that. On Android 12+ the tones come from the
 * platform's neutral palette; `system_neutral1_*` is the public form of `material_dynamic_neutral*`
 * and needs no Material Components dependency.
 */
@Suppress("MagicNumber") // The literals are the colours themselves.
data object ContrastTextColors {
    val Dark: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isDynamicColorSupported()) {
            colorResource(android.R.color.system_neutral1_900)
        } else {
            Color(0xFF191C1E)
        }

    val Bright: Color
        @Composable
        @ReadOnlyComposable
        get() = when {
            isDynamicColorSupported() && LocalUseDarkTheme.current ->
                colorResource(android.R.color.system_neutral1_100)

            isDynamicColorSupported() -> colorResource(android.R.color.system_neutral1_50)

            LocalUseDarkTheme.current -> Color(0xFFE1E3E5)

            else -> Color(0xFFF0F0F3)
        }
}
