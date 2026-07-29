/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:49 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:47 PM
 */

package dev.thomas_kiljanczyk.lyriccast.repositories

import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Setlist
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

class SetlistsRepositoryFakeImpl @Inject constructor() : SetlistsRepository {
    private val setlists = mutableListOf<Setlist>()
    private val setlistFlow = MutableStateFlow(setlists.toList())


    override fun getAllSetlists(): Flow<List<Setlist>> {
        return setlistFlow
    }

    override suspend fun getSetlist(id: UUID): Setlist? {
        return setlists.firstOrNull { it.id == id }
    }

    override suspend fun upsertSetlist(setlist: Setlist) {
        val existingSetlist = setlists.find { it.id == setlist.id }
        if (existingSetlist != null) {
            setlists.remove(existingSetlist)
        }

        setlists += setlist
        setlistFlow.emit(setlists.toList())
    }

    override suspend fun deleteSetlists(setlistIds: Collection<UUID>) {
        setlists.removeIf { it.id in setlistIds }
        setlistFlow.emit(setlists.toList())
    }
}