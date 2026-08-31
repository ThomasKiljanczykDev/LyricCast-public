package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SongDao
import dev.thomas_kiljanczyk.lyriccast.core.database.model.LyricsSectionEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SongEntity
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class SongsRepositoryRoomImpl @Inject constructor(
    private val songDao: SongDao,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : SongsRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs()
            .map { songs -> songs.map { it.toGenericModel() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun getSong(id: UUID): Song? {
        return withContext(ioDispatcher) {
            runCatching {
                songDao.getSong(id)?.toGenericModel()
            }.getOrNull()
        }
    }

    override suspend fun upsertSong(song: Song) {
        withContext(ioDispatcher) {
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
        withContext(ioDispatcher) {
            songDao.deleteSongs(songIds)
        }
    }
}
