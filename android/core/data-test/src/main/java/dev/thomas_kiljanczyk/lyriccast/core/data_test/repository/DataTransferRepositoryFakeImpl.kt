package dev.thomas_kiljanczyk.lyriccast.core.data_test.repository

import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepositoryBaseImpl
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first

class DataTransferRepositoryFakeImpl @Inject constructor(
    private val songsRepository: SongsRepository,
    private val setlistsRepository: SetlistsRepository,
    private val categoriesRepository: CategoriesRepository,
    @Dispatcher(LyricCastDispatchers.Default) defaultDispatcher: CoroutineDispatcher
) : DataTransferRepositoryBaseImpl(defaultDispatcher) {
    override suspend fun getAllSongs(): List<Song> {
        return songsRepository.getAllSongs().first()
    }

    override suspend fun getAllSetlists(): List<Setlist> {
        return setlistsRepository.getAllSetlists().first()
    }

    override suspend fun getAllCategories(): List<Category> {
        return categoriesRepository.getAllCategories().first()
    }

    override suspend fun upsertSongs(songs: Iterable<Song>) {
        songs.forEach { songsRepository.upsertSong(it) }
    }

    override suspend fun upsertSetlists(setlists: Iterable<Setlist>) {
        setlists.forEach { setlistsRepository.upsertSetlist(it) }
    }

    override suspend fun upsertCategories(categories: Iterable<Category>) {
        categories.forEach { categoriesRepository.upsertCategory(it) }
    }

    override suspend fun clearDatabase() {
        val setlistIds = getAllSetlists().map { it.id }
        setlistsRepository.deleteSetlists(setlistIds)

        val songIds = getAllSongs().map { it.id }
        songsRepository.deleteSongs(songIds)

        val categoryIds = getAllCategories().map { it.id }
        categoriesRepository.deleteCategories(categoryIds)
    }
}
