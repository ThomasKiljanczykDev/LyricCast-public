package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SetlistDao
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

internal class SetlistsRepositoryRoomImpl @Inject constructor(
    private val setlistDao: SetlistDao,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : SetlistsRepository {

    override fun getAllSetlists(): Flow<List<Setlist>> {
        return setlistDao.getAllSetlists()
            .transform { setlistEntities ->
                val setlists = withContext(ioDispatcher) {
                    setlistEntities.map { setlistEntity ->
                        val songs = setlistDao.getSongsForSetlist(setlistEntity.id)
                        Setlist(
                            id = setlistEntity.id,
                            name = setlistEntity.name,
                            presentation = songs.map { it.toGenericModel() }
                        )
                    }
                }
                emit(setlists)
            }
    }

    override suspend fun getSetlist(id: UUID): Setlist? {
        return runCatching {
            withContext(ioDispatcher) {
                val setlistEntity = setlistDao.getSetlist(id) ?: return@withContext null
                val songs = setlistDao.getSongsForSetlist(setlistEntity.id)
                Setlist(
                    id = setlistEntity.id,
                    name = setlistEntity.name,
                    presentation = songs.map { it.toGenericModel() }
                )
            }
        }.getOrNull()
    }

    override suspend fun upsertSetlist(setlist: Setlist) {
        withContext(ioDispatcher) {
            val setlistEntity = SetlistEntity(setlist)
            val songIds = setlist.presentation.map { it.id }
            setlistDao.upsertSetlistWithSongs(setlistEntity, songIds)
        }
    }

    override suspend fun deleteSetlists(setlistIds: Collection<UUID>) {
        withContext(ioDispatcher) {
            setlistDao.deleteSetlists(setlistIds)
        }
    }
}
