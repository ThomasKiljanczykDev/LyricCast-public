/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.shared.preview

import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.SetlistSongItem

/**
 * Feature-specific preview data for the setlist module that extends the core PreviewData
 * with types specific to this feature module.
 */
object SetlistPreviewData {
    val sampleSetlistSongItems = listOf(
        SetlistSongItem(song = PreviewData.amazingGrace, isSelected = false),
        SetlistSongItem(song = PreviewData.howGreatThouArt, isSelected = true),
        SetlistSongItem(song = PreviewData.beStillMySoul, isSelected = false)
    )
}
