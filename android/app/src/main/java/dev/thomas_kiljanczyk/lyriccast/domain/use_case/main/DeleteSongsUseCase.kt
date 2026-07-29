/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:35 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.main

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.DeleteSongsResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for deleting songs with business logic validation.
 * Checks if songs are in use by setlists before deletion.
 */
class DeleteSongsUseCase @Inject constructor(
    private val songsRepository: SongsRepository,
    private val setlistsRepository: SetlistsRepository
) {
    private companion object {
        const val TAG = "DeleteSongsUseCase"
    }

    /**
     * Deletes the specified songs after validation.
     *
     * @param songIds List of song IDs to delete
     * @return Result indicating success, songs in use, or error
     */
    suspend operator fun invoke(
        songIds: List<UUID>
    ): DeleteSongsResult {
        if (songIds.isEmpty()) {
            return DeleteSongsResult.Success(0)
        }

        return try {
            // Check if any songs are currently used by setlists
            val songsInUse = checkSongsInUse(songIds)
            if (songsInUse.isNotEmpty()) {
                return DeleteSongsResult.SongsInUse(songsInUse)
            }

            // All songs are safe to delete
            songsRepository.deleteSongs(songIds)
            DeleteSongsResult.Success(songIds.size)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete songs", e)
            DeleteSongsResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.delete_songs_failed)
            )
        }
    }

    /**
     * Checks which of the given song IDs are currently in use by setlists.
     *
     * @param songIds List of song IDs to check
     * @return List of song IDs that are in use
     */
    private suspend fun checkSongsInUse(songIds: List<UUID>): List<UUID> {
        return try {
            val allSetlists = setlistsRepository.getAllSetlists().first()
            val usedSongIds = allSetlists.flatMap { setlist ->
                setlist.presentation.map { it.id }
            }.toSet()
            songIds.filter { songId -> songId in usedSongIds }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check song usage, assuming none are in use", e)
            // If we can't check usage, assume none are in use to avoid blocking deletion
            // The actual deletion will fail safely at the repository level if there are constraints
            emptyList()
        }
    }
}