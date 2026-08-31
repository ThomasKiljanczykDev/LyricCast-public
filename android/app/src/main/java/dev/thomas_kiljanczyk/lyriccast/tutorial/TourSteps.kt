package dev.thomas_kiljanczyk.lyriccast.tutorial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.R
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourCardPosition
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourExpandable
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourStep
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.navigation.MainRoute
import dev.thomas_kiljanczyk.lyriccast.navigation.LyricCastAppState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * The guided tour:
 * Main → song editor → setlist editor → Settings.
 *
 * Lives in `:app` because it is the only layer that sees every feature.
 *
 * Every step targets chrome that exists on a fresh install,
 * so nothing here depends on the user already having songs.
 */
@Composable
fun rememberTourSteps(appState: LyricCastAppState): ImmutableList<TourStep> =
    remember(appState) {
        // Return to Main first, so the tour never stacks two editors.
        // Nothing has been typed, so nothing is lost.
        val toMain: () -> Unit = {
            appState.navController.popBackStack(MainRoute, inclusive = false)
        }

        persistentListOf(
            TourStep(
                id = "main_tabs",
                titleRes = R.string.tour_main_tabs_title,
                bodyRes = R.string.tour_main_tabs_body,
                anchor = TourAnchor.MAIN_TABS,
                onEnter = toMain
            ),
            TourStep(
                id = "main_fab",
                titleRes = R.string.tour_main_fab_title,
                bodyRes = R.string.tour_main_fab_body,
                anchor = TourAnchor.MAIN_FAB_MENU
            ),
            TourStep(
                id = "main_overflow",
                titleRes = R.string.tour_main_overflow_title,
                bodyRes = R.string.tour_main_overflow_body,
                anchor = TourAnchor.MAIN_OVERFLOW
            ),
            TourStep(
                id = "main_categories",
                titleRes = R.string.tour_main_categories_title,
                bodyRes = R.string.tour_main_categories_body,
                anchor = TourAnchor.MAIN_MENU_CATEGORIES
            ),
            TourStep(
                id = "main_settings",
                titleRes = R.string.tour_main_settings_title,
                bodyRes = R.string.tour_main_settings_body,
                anchor = TourAnchor.MAIN_MENU_SETTINGS
            ),
            TourStep(
                id = "main_cast",
                titleRes = R.string.tour_main_cast_title,
                bodyRes = R.string.tour_main_cast_body,
                anchor = TourAnchor.MAIN_CAST_BUTTON,
                // The Cast button hides until a receiver is discovered,
                // common indoors on first run,
                // and casting is too central to silently drop.
                keepWhenAnchorMissing = true
            ),
            TourStep(
                id = "main_sessions",
                titleRes = R.string.tour_main_sessions_title,
                bodyRes = R.string.tour_main_sessions_body,
                anchor = TourAnchor.MAIN_SESSION_BUTTON
            ),

            TourStep(
                id = "song_editor_title",
                titleRes = R.string.tour_song_editor_title_title,
                bodyRes = R.string.tour_song_editor_title_body,
                anchor = TourAnchor.SONG_EDITOR_TITLE,
                onEnter = { appState.navigateToSongEditor(null) }
            ),
            TourStep(
                id = "song_editor_structure",
                titleRes = R.string.tour_song_editor_structure_title,
                bodyRes = R.string.tour_song_editor_structure_body,
                anchor = TourAnchor.SONG_EDITOR_STRUCTURE
            ),
            TourStep(
                id = "song_editor_save",
                titleRes = R.string.tour_song_editor_save_title,
                bodyRes = R.string.tour_song_editor_save_body,
                anchor = TourAnchor.SONG_EDITOR_SAVE
            ),

            TourStep(
                id = "setlist_editor_name",
                titleRes = R.string.tour_setlist_editor_name_title,
                bodyRes = R.string.tour_setlist_editor_name_body,
                anchor = TourAnchor.SETLIST_EDITOR_NAME,
                onEnter = {
                    toMain()
                    appState.navigateToSetlistEditor(null, null)
                }
            ),
            TourStep(
                id = "setlist_editor_picker",
                titleRes = R.string.tour_setlist_editor_picker_title,
                bodyRes = R.string.tour_setlist_editor_picker_body,
                anchor = TourAnchor.SETLIST_EDITOR_SONG_PICKER,
                // Empty on a fresh install,
                // but the explanation of ordering still matters.
                keepWhenAnchorMissing = true
            ),

            TourStep(
                id = "settings_appearance",
                titleRes = R.string.tour_settings_appearance_title,
                bodyRes = R.string.tour_settings_appearance_body,
                anchor = TourAnchor.SETTINGS_APPEARANCE,
                onEnter = {
                    toMain()
                    appState.navigateToSettings()
                }
            ),
            TourStep(
                id = "settings_blank",
                titleRes = R.string.tour_settings_blank_title,
                bodyRes = R.string.tour_settings_blank_body,
                anchor = TourAnchor.SETTINGS_BLANK_ON_START
            ),
            TourStep(
                id = "settings_donate",
                titleRes = R.string.tour_settings_donate_title,
                bodyRes = R.string.tour_settings_donate_body,
                anchor = TourAnchor.SETTINGS_DONATE
            )
        )
    }

/**
 * Which collapsible container, if any, must be open for [anchor] to have bounds.
 *
 * Derived from the anchor rather than stored on the step,
 * so the two cannot drift.
 * The container's own trigger opens it too,
 * so that step shows the options it reveals rather than a closed control.
 */
fun expandableFor(anchor: TourAnchor?): TourExpandable? = when (anchor) {
    TourAnchor.MAIN_OVERFLOW,
    TourAnchor.MAIN_MENU_IMPORT,
    TourAnchor.MAIN_MENU_EXPORT,
    TourAnchor.MAIN_MENU_CATEGORIES,
    TourAnchor.MAIN_MENU_SOFT_DELETE,
    TourAnchor.MAIN_MENU_SETTINGS -> TourExpandable.MAIN_OVERFLOW_MENU

    TourAnchor.MAIN_FAB,
    TourAnchor.MAIN_FAB_MENU,
    TourAnchor.MAIN_FAB_ADD_SONG,
    TourAnchor.MAIN_FAB_ADD_SETLIST -> TourExpandable.MAIN_FAB_MENU

    else -> null
}

/**
 * Where to put the tooltip card for a step that forces [expandable] open.
 *
 * An open menu draws above the tour overlay,
 * so a card beside the control that opened it would be hidden.
 * Pin to the edge the menu grows away from.
 */
fun cardPositionFor(expandable: TourExpandable?): TourCardPosition = when (expandable) {
    // Drops down from the top app bar.
    TourExpandable.MAIN_OVERFLOW_MENU -> TourCardPosition.BOTTOM

    // Expands upwards from the floating action button.
    TourExpandable.MAIN_FAB_MENU -> TourCardPosition.TOP

    null -> TourCardPosition.AUTO
}

/**
 * Whether the scrim can cut a hole around [anchor].
 *
 * Overflow items sit in a popup drawn above the overlay,
 * so a cutout only puts a bright hole behind an already undimmed menu;
 * those items highlight themselves instead.
 * The FAB menu is inline content and cuts out like anything else.
 */
fun drawsSpotlightFor(anchor: TourAnchor?): Boolean = when (anchor) {
    TourAnchor.MAIN_MENU_IMPORT,
    TourAnchor.MAIN_MENU_EXPORT,
    TourAnchor.MAIN_MENU_CATEGORIES,
    TourAnchor.MAIN_MENU_SOFT_DELETE,
    TourAnchor.MAIN_MENU_SETTINGS -> false

    else -> true
}
