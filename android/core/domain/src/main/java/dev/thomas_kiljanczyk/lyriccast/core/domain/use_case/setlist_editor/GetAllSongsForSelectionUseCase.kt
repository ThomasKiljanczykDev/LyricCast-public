/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:35 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor

import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Projects the stored song stream into [SongItem]s with selection state and emits them sorted.
 *
 * [SongItem.fromSong] is real per-song work, so this use case runs it on [defaultDispatcher] and
 * sorts deterministically. The hop lives here rather than in the callers: both the songs list and
 * the setlist song-selection dialog consume this, so the "how a stored song becomes a list item"
 * contract — including the off-main invariant — stays in one place, and callers can collect on
 * the main dispatcher and assign Compose state directly.
 */
class GetAllSongsForSelectionUseCase @Inject constructor(
    private val songsRepository: SongsRepository,
    @param:Dispatcher(LyricCastDispatchers.Default)
    private val defaultDispatcher: CoroutineDispatcher
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
                withContext(defaultDispatcher) {
                    songs.map { song ->
                        SongItem.fromSong(song, song.id in initialSelectedIds)
                    }.sorted()
                }
            }
    }
}
