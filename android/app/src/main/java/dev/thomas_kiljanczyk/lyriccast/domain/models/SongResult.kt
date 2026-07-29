/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:23 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.models

import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import dev.thomas_kiljanczyk.lyriccast.ui.song_editor.LyricsSection
import java.util.UUID

/**
 * Result of saving a song operation.
 */
sealed class SaveSongResult {
    data class Success(val song: Song) : SaveSongResult()
    data class ValidationError(val message: UiText) : SaveSongResult()
    data class Error(val message: UiText) : SaveSongResult()
}

/**
 * Result of loading a song operation.
 */
sealed class LoadSongResult {
    data class Success(
        val song: Song,
        val sections: List<LyricsSection>
    ) : LoadSongResult()

    data class Error(val message: UiText) : LoadSongResult()
}

/**
 * Result of deleting songs operation.
 */
sealed class DeleteSongsResult {
    data class Success(val deletedCount: Int) : DeleteSongsResult()
    data class SongsInUse(val songIds: List<UUID>) : DeleteSongsResult()
    data class Error(val message: UiText) : DeleteSongsResult()
}