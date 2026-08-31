
package dev.thomas_kiljanczyk.lyriccast.feature.song.impl.model

import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor.LyricsSection

sealed class SaveSongResult {
    data class Success(val song: Song) : SaveSongResult()
    data class ValidationError(val message: UiText) : SaveSongResult()
    data class Error(val message: UiText) : SaveSongResult()
}

sealed class LoadSongResult {
    data class Success(
        val song: Song,
        val sections: List<LyricsSection>
    ) : LoadSongResult()

    data class Error(val message: UiText) : LoadSongResult()
}
