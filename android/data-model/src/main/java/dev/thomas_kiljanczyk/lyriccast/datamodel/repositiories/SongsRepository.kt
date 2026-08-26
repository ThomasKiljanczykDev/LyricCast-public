/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories

import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SongsRepository {

    fun getAllSongs(): Flow<List<Song>>

    suspend fun getSong(id: UUID): Song?

    suspend fun upsertSong(song: Song)

    suspend fun deleteSongs(songIds: Collection<UUID>)
}
