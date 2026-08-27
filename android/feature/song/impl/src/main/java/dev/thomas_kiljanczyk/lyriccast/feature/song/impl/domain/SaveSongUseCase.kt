/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:26 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.song.impl.domain

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.song_editor.ValidateSongTitleUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.model.SaveSongResult
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor.LyricsSection
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for saving songs with validation and data transformation.
 */
class SaveSongUseCase @Inject constructor(
    private val songsRepository: SongsRepository,
    private val validateSongTitleUseCase: ValidateSongTitleUseCase
) {
    private companion object {
        const val TAG = "SaveSongUseCase"
    }

    /**
     * Saves a song with validation.
     *
     * @param songId The ID of the song (empty string for new songs)
     * @param title The song title
     * @param sections List of lyrics sections
     * @param category The selected category (can be null)
     * @param existingTitles Set of existing song titles for validation
     * @param currentTitle Current title being edited (for validation)
     * @return SaveSongResult indicating success or validation errors
     */
    suspend operator fun invoke(
        songId: UUID?,
        title: String,
        sections: List<LyricsSection>,
        category: CategoryItem?,
        existingTitles: Set<String>,
        currentTitle: String? = null
    ): SaveSongResult {
        return try {
            // Validate all sections have non-empty names
            if (sections.any { it.name.isBlank() }) {
                return SaveSongResult.ValidationError(
                    UiText.StringResource(R.string.song_editor_sections_must_have_names)
                )
            }

            // Validate song title
            val titleValidation = validateSongTitleUseCase(title, existingTitles, currentTitle)
            if (titleValidation != NameValidationState.VALID) {
                val errorMessage = when (titleValidation) {
                    NameValidationState.EMPTY -> UiText.StringResource(R.string.song_editor_enter_title)

                    NameValidationState.ALREADY_IN_USE -> UiText.StringResource(R.string.song_editor_title_already_used)

                    NameValidationState.VALID ->
                        throw IllegalStateException("Unreachable: VALID title did not pass validation")
                }
                return SaveSongResult.ValidationError(errorMessage)
            }

            // Build presentation list and lyrics sections from our sections
            val presentation = sections.map { it.name }
            val lyricsMap = sections.groupBy { it.name }.map { (name, sectionList) ->
                Song.LyricsSection(name, sectionList.first().content)
            }

            val song = Song(
                id = songId ?: UUIDv7.randomUUID(),
                title = title,
                lyrics = lyricsMap,
                presentation = presentation,
                category = category?.let {
                    Category(
                        id = it.id,
                        name = it.name,
                        color = it.color
                    )
                }
            )

            songsRepository.upsertSong(song)
            SaveSongResult.Success(song)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save song", e)
            SaveSongResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.song_editor_save_failed)
            )
        }
    }
}
