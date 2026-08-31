
package dev.thomas_kiljanczyk.lyriccast.feature.song.impl.domain

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.model.LoadSongResult
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor.LyricsSection
import java.util.UUID
import javax.inject.Inject

class LoadSongUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    private companion object {
        const val TAG = "LoadSongUseCase"
    }

    suspend operator fun invoke(songId: UUID): LoadSongResult {
        return try {
            val song = songsRepository.getSong(songId)
                ?: return LoadSongResult.Error(
                    UiText.StringResource(R.string.song_editor_song_not_found)
                )

            Log.v(TAG, "Loaded song: $song")

            val sections = song.presentation.map { sectionName ->
                val content = song.lyricsMap[sectionName] ?: ""
                LyricsSection(name = sectionName, content = content)
            }

            LoadSongResult.Success(
                song = song,
                sections = sections
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load song", e)
            LoadSongResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.song_editor_load_failed)
            )
        }
    }
}
