/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 11:00 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:46 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.setlist_editor

import dev.thomas_kiljanczyk.lyriccast.shared.enums.NameValidationState
import javax.inject.Inject

/**
 * Use case for validating setlist names.
 * Checks if a name is empty or already in use.
 */
class ValidateSetlistNameUseCase @Inject constructor() {
    /**
     * Validates a setlist name against existing names.
     *
     * @param setlistName The name to validate
     * @param existingNames Set of already existing setlist names
     * @param currentName The current name being edited (null for new setlists)
     * @return NameValidationState indicating validation result
     */
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
