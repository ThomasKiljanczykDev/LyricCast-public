/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:35 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.setlist_editor

import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.SongItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for retrieving all songs as SongItems with selection state.
 * Used in selection dialogs where songs can be selected/deselected.
 */
class GetAllSongsForSelectionUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    /**
     * Returns a flow of all songs as SongItems with initial selection state.
     *
     * @param initialSelectedIds Set of song IDs that should be initially selected
     * @return Flow emitting a sorted list of SongItems
     */
    operator fun invoke(initialSelectedIds: Set<UUID> = emptySet()): Flow<List<SongItem>> {
        return songsRepository.getAllSongs()
            .map { songs ->
                songs.map { song ->
                    SongItem.fromSong(song, song.id in initialSelectedIds)
                }.sorted()
            }
    }
}