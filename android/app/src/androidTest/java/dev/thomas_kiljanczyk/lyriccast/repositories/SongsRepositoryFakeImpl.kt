/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:49 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:46 PM
 */

package dev.thomas_kiljanczyk.lyriccast.repositories

import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

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