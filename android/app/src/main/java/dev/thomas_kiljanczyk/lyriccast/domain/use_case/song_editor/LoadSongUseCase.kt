/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:30 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.song_editor

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.LoadSongResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import dev.thomas_kiljanczyk.lyriccast.ui.song_editor.LyricsSection
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for loading songs and transforming them to editing format.
 */
class LoadSongUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    private companion object {
        const val TAG = "LoadSongUseCase"
    }

    /**
     * Loads a song by ID and transforms it to editing format.
     *
     * @param songId The ID of the song to load
     * @return LoadSongResult containing the loaded song data or error
     */
    suspend operator fun invoke(songId: UUID): LoadSongResult {
        return try {
            val song = songsRepository.getSong(songId)
                ?: return LoadSongResult.Error(
                    UiText.StringResource(R.string.song_editor_song_not_found)
                )

            Log.v(TAG, "Loaded song: $song")

            // Transform song data to editing format
            val sections = song.presentation.map { sectionName ->
                val content = song.lyricsMap[sectionName] ?: ""
                LyricsSection(name = sectionName, content = content)
            }

            LoadSongResult.Success(
                song = song,
                sections = sections
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load song", e)
            LoadSongResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.song_editor_load_failed)
            )
        }
    }
}