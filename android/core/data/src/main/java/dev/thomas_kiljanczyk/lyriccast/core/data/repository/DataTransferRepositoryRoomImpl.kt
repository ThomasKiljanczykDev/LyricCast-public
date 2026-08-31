package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import androidx.room.Transaction
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.CategoryDao
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SetlistDao
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SongDao
import dev.thomas_kiljanczyk.lyriccast.core.database.model.CategoryEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.LyricsSectionEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SongEntity
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

internal class DataTransferRepositoryRoomImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val songDao: SongDao,
    private val setlistDao: SetlistDao,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @Dispatcher(LyricCastDispatchers.Default) defaultDispatcher: CoroutineDispatcher
) : DataTransferRepositoryBaseImpl(defaultDispatcher) {

    override suspend fun getAllCategories(): List<Category> {
        return withContext(ioDispatcher) {
            categoryDao.getAllCategories()
                .first()
                .map { it.toGenericModel() }
        }
    }

    override suspend fun getAllSongs(): List<Song> {
        return withContext(ioDispatcher) {
            songDao.getAllSongs()
                .first()
                .map { it.toGenericModel() }
        }
    }

    override suspend fun getAllSetlists(): List<Setlist> {
        return withContext(ioDispatcher) {
            val setlistEntities = setlistDao.getAllSetlists().first()
            setlistEntities.map { setlistEntity ->
                val songs = setlistDao.getSongsForSetlist(setlistEntity.id)
                Setlist(
                    id = setlistEntity.id,
                    name = setlistEntity.name,
                    presentation = songs.map { it.toGenericModel() }
                )
            }
        }
    }

    @Transaction
    override suspend fun clearDatabase() {
        withContext(ioDispatcher) {
            // Note: Due to foreign key constraints, we need to delete in the right order
            setlistDao.deleteAllSetlistSongs()

            setlistDao.deleteAllSetlists()

            songDao.deleteAllLyricsSections()

            songDao.deleteAllSongs()

            // 5. Delete categories (can be done last as songs reference categories)
            categoryDao.deleteAllCategories()
        }
    }

    override suspend fun upsertCategories(categories: Iterable<Category>) {
        withContext(ioDispatcher) {
            val categoryEntities = categories.map { CategoryEntity(it) }
            categoryDao.upsertCategories(categoryEntities)
        }
    }

    override suspend fun upsertSongs(songs: Iterable<Song>) {
        withContext(ioDispatcher) {
            val songsWithLyrics = songs.associate { song ->
                val songEntity = SongEntity(song)
                val lyricsSections = song.lyrics.mapIndexed { index, section ->
                    LyricsSectionEntity(
                        songId = songEntity.id,
                        name = section.name,
                        text = section.text,
                        orderIndex = index
                    )
                }
                songEntity to lyricsSections
            }

            songDao.upsertSongsWithLyrics(songsWithLyrics)
        }
    }

    override suspend fun upsertSetlists(setlists: Iterable<Setlist>) {
        withContext(ioDispatcher) {
            val setlistsWithSongs = setlists.associate { setlist ->
                val setlistEntity = SetlistEntity(setlist)
                val songIds = setlist.presentation.map { it.id }
                setlistEntity to songIds
            }

            setlistDao.upsertSetlistsWithSongs(setlistsWithSongs)
        }
    }
}
