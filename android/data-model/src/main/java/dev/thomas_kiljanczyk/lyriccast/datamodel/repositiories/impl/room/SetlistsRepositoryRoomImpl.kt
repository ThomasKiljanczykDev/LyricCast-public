/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:18 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.impl.room

import dev.thomas_kiljanczyk.lyriccast.datamodel.dao.SetlistDao
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Setlist
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import java.util.UUID

internal class SetlistsRepositoryRoomImpl(
    private val setlistDao: SetlistDao
) : SetlistsRepository {

    override fun getAllSetlists(): Flow<List<Setlist>> {
        return setlistDao.getAllSetlists()
            .transform { setlistEntities ->
                val setlists = withContext(Dispatchers.IO) {
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
            withContext(Dispatchers.IO) {
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
        withContext(Dispatchers.IO) {
            val setlistEntity = SetlistEntity(setlist)
            val songIds = setlist.presentation.map { it.id }
            setlistDao.upsertSetlistWithSongs(setlistEntity, songIds)
        }
    }

    override suspend fun deleteSetlists(setlistIds: Collection<UUID>) {
        withContext(Dispatchers.IO) {
            setlistDao.deleteSetlists(setlistIds)
        }
    }
}