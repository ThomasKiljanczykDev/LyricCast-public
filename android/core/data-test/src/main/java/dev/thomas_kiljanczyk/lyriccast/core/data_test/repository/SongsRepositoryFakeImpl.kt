/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:49 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:46 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data_test.repository

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SongsRepositoryFakeImpl @Inject constructor() : SongsRepository {
    private val songs = mutableListOf<Song>()
    private val songFlow = MutableStateFlow(songs.toList())

    override fun getAllSongs(): Flow<List<Song>> {
        return songFlow
    }

    override suspend fun getSong(id: UUID): Song? {
        return songs.firstOrNull { it.id == id }
    }

    override suspend fun upsertSong(song: Song) {
        val existingSong = songs.find { it.id == song.id }
        if (existingSong != null) {
            songs.remove(existingSong)
        }

        songs += song
        songFlow.emit(songs.toList())
    }

    override suspend fun deleteSongs(songIds: Collection<UUID>) {
        songs.removeIf { it.id in songIds }
        songFlow.emit(songs.toList())
    }
}
