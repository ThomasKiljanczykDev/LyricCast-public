/*
 * Created by Tomasz Kiljanczyk on 9/11/25, 9:27 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/11/25, 9:25 AM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.LyricsSectionEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SongEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SongWithLyricsAndCategory
import java.util.UUID
import kotlinx.coroutines.flow.Flow

// SQLite caps the number of bound query parameters (~999); chunk well below that limit.
private const val DELETE_CHUNK_SIZE = 500

@Dao
interface SongDao {
    @Transaction
    @Query("SELECT * FROM songs ORDER BY title")
    fun getAllSongs(): Flow<List<SongWithLyricsAndCategory>>

    @Transaction
    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSong(id: UUID): SongWithLyricsAndCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyricsSections(sections: List<LyricsSectionEntity>)

    @Query("DELETE FROM lyrics_sections WHERE songId = :songId")
    suspend fun deleteLyricsSectionsForSong(songId: UUID)

    @Query("DELETE FROM lyrics_sections WHERE songId IN (:songIds)")
    suspend fun deleteLyricsSectionsForSongs(songIds: Collection<UUID>)

    @Query("DELETE FROM lyrics_sections")
    suspend fun deleteAllLyricsSections()

    @Transaction
    suspend fun upsertSongWithLyrics(song: SongEntity, lyricsSections: List<LyricsSectionEntity>) {
        insertSong(song)
        deleteLyricsSectionsForSong(song.id)
        insertLyricsSections(lyricsSections)
    }

    @Transaction
    suspend fun upsertSongsWithLyrics(songsWithLyrics: Map<SongEntity, List<LyricsSectionEntity>>) {
        // Insert all songs in bulk
        insertSongs(songsWithLyrics.keys.toList())

        // Delete existing lyrics for all songs in bulk
        val songIds = songsWithLyrics.keys.map { it.id }
        if (songIds.isNotEmpty()) {
            songIds.chunked(DELETE_CHUNK_SIZE).forEach { songIdsChunk ->
                deleteLyricsSectionsForSongs(songIdsChunk)
            }
        }

        // Insert all lyrics sections in bulk
        val allLyricsSections = songsWithLyrics.values.flatten()
        if (allLyricsSections.isNotEmpty()) {
            insertLyricsSections(allLyricsSections)
        }
    }

    @Query("DELETE FROM songs WHERE id IN (:songIds)")
    suspend fun deleteSongs(songIds: Collection<UUID>)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Delete
    suspend fun deleteSong(song: SongEntity)
}
