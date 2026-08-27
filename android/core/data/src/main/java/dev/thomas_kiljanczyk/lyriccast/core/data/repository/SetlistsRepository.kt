/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SetlistsRepository {

    fun getAllSetlists(): Flow<List<Setlist>>

    suspend fun getSetlist(id: UUID): Setlist?

    suspend fun upsertSetlist(setlist: Setlist)

    suspend fun deleteSetlists(setlistIds: Collection<UUID>)
}
