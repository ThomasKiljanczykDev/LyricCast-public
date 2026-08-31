package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Whether the current step points at a given anchor.
 *
 * Popups draw in their own window above the overlay,
 * so nothing outside can dim or cut them out:
 * a target inside one has to mark itself.
 */
@Stable
fun interface TourSpotlight {
    fun isSpotlit(anchor: TourAnchor): Boolean

    companion object {
        val None = TourSpotlight { false }
    }
}

val LocalTourSpotlight = compositionLocalOf { TourSpotlight.None }

@Composable
fun Modifier.tourSpotlight(anchor: TourAnchor): Modifier =
    if (LocalTourSpotlight.current.isSpotlit(anchor)) {
        background(MaterialTheme.colorScheme.secondaryContainer)
    } else {
        this
    }
