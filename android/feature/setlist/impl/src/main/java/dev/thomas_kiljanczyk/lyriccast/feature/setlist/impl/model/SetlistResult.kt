/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:33 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model

import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText

/**
 * Result of loading a setlist operation.
 */
sealed class LoadSetlistResult {
    data class Success(
        val setlist: Setlist,
        val setlistSongItems: List<SetlistSongItem>
    ) : LoadSetlistResult()

    data class Error(val message: UiText) : LoadSetlistResult()
}
