/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:13 PM
 */

package dev.thomas_kiljanczyk.lyriccast.navigation

/**
 * Deep link URI patterns for the LyricCast app
 */
object DeepLinkConstants {
    const val SCHEME = "lyriccast"
    const val HOST = "app"

    // Deep link patterns
    const val MAIN_PATTERN = "$SCHEME://$HOST/main"
    const val SONG_EDITOR_PATTERN = "$SCHEME://$HOST/song/editor?songId={songId}"
    const val SONG_CONTROLS_PATTERN = "$SCHEME://$HOST/song/controls/{songId}"
    const val SETLIST_EDITOR_PATTERN = "$SCHEME://$HOST/setlist/editor?setlistId={setlistId}"
    const val SETLIST_CONTROLS_PATTERN = "$SCHEME://$HOST/setlist/controls/{setlistId}"
    const val SETTINGS_PATTERN = "$SCHEME://$HOST/settings"
    const val CATEGORY_MANAGER_PATTERN = "$SCHEME://$HOST/categories"
    const val SESSION_CLIENT_PATTERN = "$SCHEME://$HOST/session"
}
