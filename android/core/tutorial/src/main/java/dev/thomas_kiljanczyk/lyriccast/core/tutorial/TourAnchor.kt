package dev.thomas_kiljanczyk.lyriccast.core.tutorial

/**
 * Identifies a UI element the guided tour can spotlight.
 *
 * Declared here, not per-feature,
 * so the step list in `:app` can reference them without a feature-to-feature dependency.
 *
 * Adaptive layouts reuse the same id:
 * the rail and the bottom bar both tag their items with [MAIN_TABS],
 * so a window size change mid-tour just republishes bounds.
 */
enum class TourAnchor {
    MAIN_TABS,
    MAIN_JOIN_SESSION_TAB,
    MAIN_FAB,
    MAIN_FAB_MENU,
    MAIN_FAB_ADD_SONG,
    MAIN_FAB_ADD_SETLIST,
    MAIN_OVERFLOW,
    MAIN_MENU_IMPORT,
    MAIN_MENU_EXPORT,
    MAIN_MENU_CATEGORIES,
    MAIN_MENU_SOFT_DELETE,
    MAIN_MENU_SETTINGS,
    MAIN_SESSION_BUTTON,
    MAIN_CAST_BUTTON,

    SONG_EDITOR_TITLE,
    SONG_EDITOR_LYRICS,
    SONG_EDITOR_STRUCTURE,
    SONG_EDITOR_CATEGORIES,
    SONG_EDITOR_SAVE,

    SETLIST_EDITOR_NAME,
    SETLIST_EDITOR_SONG_PICKER,

    SETTINGS_APPEARANCE,
    SETTINGS_BLANK_ON_START,
    SETTINGS_DONATE
}
