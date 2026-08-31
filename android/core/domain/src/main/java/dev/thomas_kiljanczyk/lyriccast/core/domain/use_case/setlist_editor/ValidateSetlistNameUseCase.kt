package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor

import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import javax.inject.Inject

class ValidateSetlistNameUseCase @Inject constructor() {
    operator fun invoke(
        setlistName: String,
        existingNames: Set<String>,
        currentName: String? = null
    ): NameValidationState {
        if (setlistName.isBlank()) {
            return NameValidationState.EMPTY
        }

        val isAlreadyInUse = currentName != setlistName && setlistName in existingNames

        return if (isAlreadyInUse) {
            NameValidationState.ALREADY_IN_USE
        } else {
            NameValidationState.VALID
        }
    }
}
