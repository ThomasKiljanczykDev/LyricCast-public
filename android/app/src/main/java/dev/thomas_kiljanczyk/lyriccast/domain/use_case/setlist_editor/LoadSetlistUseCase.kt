/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:05 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:57 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.setlist_editor

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.LoadSetlistResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.SongItem
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import dev.thomas_kiljanczyk.lyriccast.ui.setlist_editor.SetlistSongItem
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for loading setlists and transforming them to editing format.
 */
class LoadSetlistUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository,
    private val getSongsByIdsUseCase: GetSongsByIdsUseCase
) {
    private companion object {
        const val TAG = "LoadSetlistUseCase"
    }

    /**
     * Loads a setlist by ID and transforms it to editing format.
     *
     * @param setlistId The ID of the setlist to load
     * @return LoadSetlistResult containing the loaded setlist data or error
     */
    suspend operator fun invoke(
        setlistId: UUID
    ): LoadSetlistResult {
        return try {
            val setlist = setlistsRepository.getSetlist(setlistId)
                ?: return LoadSetlistResult.Error(
                    UiText.StringResource(R.string.setlist_editor_setlist_not_found)
                )

            Log.v(TAG, "Loaded setlist: $setlist")

            // Transform setlist songs to editing format
            val songIds = setlist.presentation.map { it.id }
            val songs = getSongsByIdsUseCase(songIds)

            val setlistSongItems = songs.map { song ->
                SetlistSongItem(SongItem.fromSong(song))
            }

            LoadSetlistResult.Success(
                setlist = setlist,
                setlistSongItems = setlistSongItems
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load setlist", e)
            LoadSetlistResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.setlist_editor_load_failed)
            )
        }
    }
}
