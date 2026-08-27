/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for retrieving songs by their IDs.
 * Filters out songs that don't exist and logs missing songs.
 */
class GetSongsByIdsUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    private companion object {
        const val TAG = "GetSongsByIdsUseCase"
    }

    /**
     * Retrieves songs by their IDs.
     *
     * @param songIds List of song IDs to retrieve
     * @return List of songs that exist, in the same order as the input IDs
     */
    suspend operator fun invoke(songIds: List<UUID>): List<Song> {
        return try {
            val songs = songIds.mapNotNull { songId ->
                val song = songsRepository.getSong(songId)
                if (song == null) {
                    Log.w(TAG, "Song with ID $songId not found")
                }
                song
            }
            songs
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve songs by IDs", e)
            emptyList()
        }
    }
}
