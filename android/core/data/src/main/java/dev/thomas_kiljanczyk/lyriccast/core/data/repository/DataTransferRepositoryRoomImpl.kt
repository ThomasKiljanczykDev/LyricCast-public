/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:19 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import androidx.room.Transaction
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

internal class DataTransferRepositoryRoomImpl(
    private val categoryDao: CategoryDao,
    private val songDao: SongDao,
    private val setlistDao: SetlistDao
) : DataTransferRepositoryBaseImpl() {

    override suspend fun getAllCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAllCategories()
                .first()
                .map { it.toGenericModel() }
        }
    }

    override suspend fun getAllSongs(): List<Song> {
        return withContext(Dispatchers.IO) {
            songDao.getAllSongs()
                .first()
                .map { it.toGenericModel() }
        }
    }

    override suspend fun getAllSetlists(): List<Setlist> {
        return withContext(Dispatchers.IO) {
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
        withContext(Dispatchers.IO) {
            // Clear all data from all tables using bulk operations
            // Note: Due to foreign key constraints, we need to delete in the right order
            // 1. Delete setlist songs (cross-references)
            setlistDao.deleteAllSetlistSongs()

            // 2. Delete setlists
            setlistDao.deleteAllSetlists()

            // 3. Delete lyrics sections
            songDao.deleteAllLyricsSections()

            // 4. Delete songs
            songDao.deleteAllSongs()

            // 5. Delete categories (can be done last as songs reference categories)
            categoryDao.deleteAllCategories()
        }
    }

    override suspend fun upsertCategories(categories: Iterable<Category>) {
        withContext(Dispatchers.IO) {
            val categoryEntities = categories.map { CategoryEntity(it) }
            categoryDao.upsertCategories(categoryEntities)
        }
    }

    override suspend fun upsertSongs(songs: Iterable<Song>) {
        withContext(Dispatchers.IO) {
            // Prepare all songs and their lyrics in a single map
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

            // Bulk upsert all songs and their lyrics in a single transaction
            songDao.upsertSongsWithLyrics(songsWithLyrics)
        }
    }

    override suspend fun upsertSetlists(setlists: Iterable<Setlist>) {
        withContext(Dispatchers.IO) {
            // Prepare all setlists and their songs in a single map
            val setlistsWithSongs = setlists.associate { setlist ->
                val setlistEntity = SetlistEntity(setlist)
                val songIds = setlist.presentation.map { it.id }
                setlistEntity to songIds
            }

            // Bulk upsert all setlists and their songs in a single transaction
            setlistDao.upsertSetlistsWithSongs(setlistsWithSongs)
        }
    }
}
