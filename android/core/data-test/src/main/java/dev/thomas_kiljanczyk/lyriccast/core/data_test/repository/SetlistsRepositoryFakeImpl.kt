package dev.thomas_kiljanczyk.lyriccast.core.data_test.repository

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
