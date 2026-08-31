package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SongsRepository {

    fun getAllSongs(): Flow<List<Song>>

    suspend fun getSong(id: UUID): Song?

    suspend fun upsertSong(song: Song)

    suspend fun deleteSongs(songIds: Collection<UUID>)
}
