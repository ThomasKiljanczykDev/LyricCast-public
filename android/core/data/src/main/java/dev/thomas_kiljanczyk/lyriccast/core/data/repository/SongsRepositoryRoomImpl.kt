/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:18 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SongDao
import dev.thomas_kiljanczyk.lyriccast.core.database.model.LyricsSectionEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SongEntity
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class SongsRepositoryRoomImpl(
    private val songDao: SongDao
) : SongsRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs()
            .map { songs -> songs.map { it.toGenericModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getSong(id: UUID): Song? {
        return withContext(Dispatchers.IO) {
            runCatching {
                songDao.getSong(id)?.toGenericModel()
            }.getOrNull()
        }
    }

    override suspend fun upsertSong(song: Song) {
        withContext(Dispatchers.IO) {
            val songEntity = SongEntity(song)
            val lyricsSections = song.lyrics.mapIndexed { index, section ->
                LyricsSectionEntity(
                    songId = songEntity.id,
                    name = section.name,
                    text = section.text,
                    orderIndex = index
                )
            }
            songDao.upsertSongWithLyrics(songEntity, lyricsSections)
        }
    }

    override suspend fun deleteSongs(songIds: Collection<UUID>) {
        withContext(Dispatchers.IO) {
            songDao.deleteSongs(songIds)
        }
    }
}
