
package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.shared.preview

import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SetlistInfo
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SetlistSongInfo

object SessionPreviewData {
    private val setlistSongs = listOf(
        PreviewData.amazingGrace,
        PreviewData.howGreatThouArt,
        PreviewData.beStillMySoul
    )

    val sampleSongTitle = PreviewData.amazingGrace.title

    /** The first presented slide of [sampleSongTitle], as a client would receive it. */
    val sampleSlideText = PreviewData.amazingGrace.let { song ->
        song.lyricsMap[song.presentation.first()].orEmpty()
    }

    val sampleSlideCount = PreviewData.amazingGrace.presentation.size

    val sampleSetlist = SetlistInfo(
        songs = setlistSongs.map { SetlistSongInfo(id = it.id.toString(), title = it.title) },
        currentSongIndex = 0
    )
}
