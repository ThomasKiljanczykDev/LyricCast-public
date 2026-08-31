package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf

/** A collapsible container the tour may need open to spotlight something inside it. */
enum class TourExpandable {
    MAIN_OVERFLOW_MENU,
    MAIN_FAB_MENU
}

/**
 * Lets the tour force a menu open for the duration of a step.
 *
 * Targets inside a menu only exist while it is expanded,
 * and the overlay swallows input, so the user cannot open it.
 *
 * The owning composable ORs this with its own state,
 * rather than `:app` threading an "expanded" flag through every intermediate composable:
 *
 * ```
 * val expanded = showMenu || LocalTourExpansion.current.isForcedOpen(MAIN_OVERFLOW_MENU)
 * ```
 */
@Stable
fun interface TourExpansion {
    fun isForcedOpen(expandable: TourExpandable): Boolean

    companion object {
        val None = TourExpansion { false }
    }
}

val LocalTourExpansion = compositionLocalOf { TourExpansion.None }
