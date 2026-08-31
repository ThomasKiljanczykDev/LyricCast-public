package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.SaveSetlistResult
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import java.util.UUID
import javax.inject.Inject

class SaveSetlistUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository,
    private val validateSetlistNameUseCase: ValidateSetlistNameUseCase
) {
    private companion object {
        const val TAG = "SaveSetlistUseCase"
    }

    suspend operator fun invoke(
        setlistId: UUID,
        name: String,
        songs: List<Song>,
        existingNames: Set<String>,
        currentName: String? = null
    ): SaveSetlistResult {
        return try {
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
