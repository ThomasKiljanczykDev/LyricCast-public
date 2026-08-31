
package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.domain

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.GetSongsByIdsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.LoadSetlistResult
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.SetlistSongItem
import java.util.UUID
import javax.inject.Inject

class LoadSetlistUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository,
    private val getSongsByIdsUseCase: GetSongsByIdsUseCase
) {
    private companion object {
        const val TAG = "LoadSetlistUseCase"
    }

    suspend operator fun invoke(
        setlistId: UUID
    ): LoadSetlistResult {
        return try {
            val setlist = setlistsRepository.getSetlist(setlistId)
                ?: return LoadSetlistResult.Error(
                    UiText.StringResource(R.string.setlist_editor_setlist_not_found)
                )

            Log.v(TAG, "Loaded setlist: $setlist")

            val songIds = setlist.presentation.map { it.id }
            val songs = getSongsByIdsUseCase(songIds)

            val setlistSongItems = songs.map { song ->
                SetlistSongItem(SongItem.fromSong(song))
            }

            LoadSetlistResult.Success(
                setlist = setlist,
                setlistSongItems = setlistSongItems
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load setlist", e)
            LoadSetlistResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.setlist_editor_load_failed)
            )
        }
    }
}
