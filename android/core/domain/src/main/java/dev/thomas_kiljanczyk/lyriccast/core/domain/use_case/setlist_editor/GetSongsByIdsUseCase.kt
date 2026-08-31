package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID
import javax.inject.Inject

class GetSongsByIdsUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    private companion object {
        const val TAG = "GetSongsByIdsUseCase"
    }

    suspend operator fun invoke(songIds: List<UUID>): List<Song> {
        return try {
            val songs = songIds.mapNotNull { songId ->
                val song = songsRepository.getSong(songId)
                if (song == null) {
                    Log.w(TAG, "Song with ID $songId not found")
                }
                song
            }
            songs
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve songs by IDs", e)
            emptyList()
        }
    }
}
