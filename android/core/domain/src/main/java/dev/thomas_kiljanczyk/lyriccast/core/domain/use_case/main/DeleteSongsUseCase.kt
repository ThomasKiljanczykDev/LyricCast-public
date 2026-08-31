package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteSongsResult
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class DeleteSongsUseCase @Inject constructor(
    private val songsRepository: SongsRepository,
    private val setlistsRepository: SetlistsRepository
) {
    private companion object {
        const val TAG = "DeleteSongsUseCase"
    }

    suspend operator fun invoke(
        songIds: List<UUID>
    ): DeleteSongsResult {
        if (songIds.isEmpty()) {
            return DeleteSongsResult.Success(0)
        }

        return try {
            val songsInUse = checkSongsInUse(songIds)
            if (songsInUse.isNotEmpty()) {
                return DeleteSongsResult.SongsInUse(songsInUse)
            }

            songsRepository.deleteSongs(songIds)
            DeleteSongsResult.Success(songIds.size)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete songs", e)
            DeleteSongsResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.delete_songs_failed)
            )
        }
    }

    private suspend fun checkSongsInUse(songIds: List<UUID>): List<UUID> {
        return try {
            val allSetlists = setlistsRepository.getAllSetlists().first()
            val usedSongIds = allSetlists.flatMap { setlist ->
                setlist.presentation.map { it.id }
            }.toSet()
            songIds.filter { songId -> songId in usedSongIds }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check song usage, assuming none are in use", e)
            // If we can't check usage, assume none are in use to avoid blocking deletion
            // The actual deletion will fail safely at the repository level if there are constraints
            emptyList()
        }
    }
}
