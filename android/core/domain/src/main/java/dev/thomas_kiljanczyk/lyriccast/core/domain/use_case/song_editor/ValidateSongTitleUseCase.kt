package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.song_editor

import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import javax.inject.Inject

class ValidateSongTitleUseCase @Inject constructor() {
    operator fun invoke(
        songTitle: String,
        existingTitles: Set<String>,
        currentTitle: String? = null
    ): NameValidationState {
        if (songTitle.isBlank()) {
            return NameValidationState.EMPTY
        }

        val alreadyInUse = currentTitle != songTitle && songTitle in existingTitles

        return if (alreadyInUse) {
            NameValidationState.ALREADY_IN_USE
        } else {
            NameValidationState.VALID
        }
    }
}
