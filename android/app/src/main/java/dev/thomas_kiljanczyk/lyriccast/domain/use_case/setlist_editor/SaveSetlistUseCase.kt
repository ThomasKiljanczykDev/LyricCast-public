/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:33 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.setlist_editor

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Setlist
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.SaveSetlistResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import dev.thomas_kiljanczyk.lyriccast.shared.enums.NameValidationState
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for saving setlists with validation.
 */
class SaveSetlistUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository,
    private val validateSetlistNameUseCase: ValidateSetlistNameUseCase
) {
    private companion object {
        const val TAG = "SaveSetlistUseCase"
    }

    /**
     * Saves a setlist with validation.
     *
     * @param setlistId The ID of the setlist (empty string for new setlists)
     * @param name The setlist name
     * @param songs List of songs in the setlist
     * @param existingNames Set of existing setlist names for validation
     * @param currentName Current name being edited (for validation)
     * @return SaveSetlistResult indicating success or validation errors
     */
    suspend operator fun invoke(
        setlistId: UUID,
        name: String,
        songs: List<Song>,
        existingNames: Set<String>,
        currentName: String? = null
    ): SaveSetlistResult {
        return try {
            // Validate setlist name
            val nameValidation = validateSetlistNameUseCase(name, existingNames, currentName)
            if (nameValidation != NameValidationState.VALID) {
                val errorMessage = when (nameValidation) {
                    NameValidationState.EMPTY -> UiText.StringResource(R.string.setlist_editor_enter_name)

                    NameValidationState.ALREADY_IN_USE ->
                        UiText.StringResource(R.string.setlist_editor_name_already_used)

                    NameValidationState.VALID ->
                        throw IllegalStateException("Unreachable: VALID name did not pass validation")
                }
                return SaveSetlistResult.ValidationError(errorMessage)
            }

            // Validate setlist has songs
            if (songs.isEmpty()) {
                return SaveSetlistResult.ValidationError(
                    UiText.StringResource(R.string.setlist_editor_must_have_songs)
                )
            }

            val setlist = Setlist(
                id = setlistId,
                name = name,
                presentation = songs
            )

            setlistsRepository.upsertSetlist(setlist)
            SaveSetlistResult.Success(setlist)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save setlist", e)
            SaveSetlistResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.setlist_editor_save_failed)
            )
        }
    }
}
