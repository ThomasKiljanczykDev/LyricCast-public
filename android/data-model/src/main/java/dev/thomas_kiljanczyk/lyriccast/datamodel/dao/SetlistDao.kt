/*
 * Created by Tomasz Kiljanczyk on 9/11/25, 9:27 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/11/25, 9:26 AM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SetlistSongCrossRef
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SongWithLyricsAndCategory
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SetlistDao {
    @Query("SELECT * FROM setlists ORDER BY name")
    fun getAllSetlists(): Flow<List<SetlistEntity>>

    @Query("SELECT * FROM setlists WHERE id = :id")
    suspend fun getSetlist(id: UUID): SetlistEntity?

    @Query("SELECT * FROM setlist_songs WHERE setlistId = :setlistId ORDER BY position")
    suspend fun getSetlistSongs(setlistId: UUID): List<SetlistSongCrossRef>

    @Transaction
    @Query(
        """
        SELECT s.* FROM songs s 
        INNER JOIN setlist_songs ss ON s.id = ss.songId 
        WHERE ss.setlistId = :setlistId 
        ORDER BY ss.position
    """
    )
    suspend fun getSongsForSetlist(setlistId: UUID): List<SongWithLyricsAndCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlist(setlist: SetlistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlists(setlists: List<SetlistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlistSongs(crossRefs: List<SetlistSongCrossRef>)

    @Query("DELETE FROM setlist_songs WHERE setlistId = :setlistId")
    suspend fun deleteSetlistSongs(setlistId: UUID)

    @Query("DELETE FROM setlist_songs WHERE setlistId IN (:setlistIds)")
    suspend fun deleteSetlistSongs(setlistIds: Collection<UUID>)

    @Query("DELETE FROM setlist_songs")
    suspend fun deleteAllSetlistSongs()

    @Transaction
    suspend fun upsertSetlistWithSongs(setlist: SetlistEntity, songIds: List<UUID>) {
        insertSetlist(setlist)
        deleteSetlistSongs(setlist.id)
        val crossRefs = songIds.mapIndexed { index, songId ->
            SetlistSongCrossRef(
                setlistId = setlist.id,
                songId = songId,
                position = index
            )
        }
        insertSetlistSongs(crossRefs)
    }

    @Transaction
    suspend fun upsertSetlistsWithSongs(setlistsWithSongs: Map<SetlistEntity, List<UUID>>) {
        // Insert all setlists in bulk
        insertSetlists(setlistsWithSongs.keys.toList())

        // Delete existing songs for all setlists in bulk
        val setlistIds = setlistsWithSongs.keys.map { it.id }
        if (setlistIds.isNotEmpty()) {
            setlistIds.chunked(500).forEach { setlistIdsChunk ->
                deleteSetlistSongs(setlistIdsChunk)
            }
        }

        // Insert all setlist songs in bulk
        val allCrossRefs = setlistsWithSongs.flatMap { (setlist, songIds) ->
            songIds.mapIndexed { index, songId ->
                SetlistSongCrossRef(
                    setlistId = setlist.id,
                    songId = songId,
                    position = index
                )
            }
        }
        if (allCrossRefs.isNotEmpty()) {
            insertSetlistSongs(allCrossRefs)
        }
    }

    @Query("DELETE FROM setlists WHERE id IN (:setlistIds)")
    suspend fun deleteSetlists(setlistIds: Collection<UUID>)

    @Query("DELETE FROM setlists")
    suspend fun deleteAllSetlists()

    @Delete
    suspend fun deleteSetlist(setlist: SetlistEntity)
}