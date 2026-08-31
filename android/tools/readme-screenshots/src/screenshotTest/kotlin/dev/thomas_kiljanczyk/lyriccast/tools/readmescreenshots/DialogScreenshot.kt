package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Layoutlib renders a `Dialog` inline rather than in its own window, which costs it the two things
 * that make a dialog read as a dialog on-device: the scrim over the screen behind it, and the side
 * margins `usePlatformDefaultWidth` would otherwise impose. Both are put back here so the rendered
 * shot matches what the user actually sees.
 */
private val SCRIM = Color.Black.copy(alpha = 0.6f)
private val DIALOG_MARGIN = 40.dp

/**
 * Layoutlib's `Dialog` escapes its parent's constraints, so the margin cannot come from
 * [DialogScreenshot]'s own layout -- it has to be handed to the dialog composable itself, which
 * passes it down to the `AlertDialog` surface.
 */
val DialogInsetModifier: Modifier = Modifier.padding(horizontal = DIALOG_MARGIN)

@Composable
fun DialogScreenshot(background: @Composable () -> Unit, dialog: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        background()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SCRIM)
                .padding(horizontal = DIALOG_MARGIN),
            contentAlignment = Alignment.Center
        ) {
            dialog()
        }
    }
}
