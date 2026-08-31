
package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.shared.preview

import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.SetlistSongItem

object SetlistPreviewData {
    val sampleSetlistSongItems = listOf(
        SetlistSongItem(song = PreviewData.amazingGrace, isSelected = false),
        SetlistSongItem(song = PreviewData.howGreatThouArt, isSelected = true),
        SetlistSongItem(song = PreviewData.beStillMySoul, isSelected = false)
    )
}
