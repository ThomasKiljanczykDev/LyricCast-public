package dev.thomas_kiljanczyk.lyriccast.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SetlistSongCrossRef
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SongWithLyricsAndCategory
import java.util.UUID
import kotlinx.coroutines.flow.Flow

// SQLite caps the number of bound query parameters (~999); chunk well below that limit.
private const val DELETE_CHUNK_SIZE = 500

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
        insertSetlists(setlistsWithSongs.keys.toList())

        val setlistIds = setlistsWithSongs.keys.map { it.id }
        if (setlistIds.isNotEmpty()) {
            setlistIds.chunked(DELETE_CHUNK_SIZE).forEach { setlistIdsChunk ->
                deleteSetlistSongs(setlistIdsChunk)
            }
        }

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
