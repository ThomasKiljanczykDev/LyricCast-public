/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 11:00 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:59 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.category_manager

import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.domain.models.CategoryNameValidationResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import javax.inject.Inject

/**
 * Use case for validating category names.
 * Checks if a name is empty, already in use, or exceeds maximum length.
 */
class ValidateCategoryNameUseCase @Inject constructor() {
    companion object {
        const val MAX_LENGTH = 30
    }

    /**
     * Validates a category name against business rules.
     *
     * @param input The category name to validate
     * @param existingNames Set of already existing category names
     * @return CategoryNameValidationResult with success/failure and error message
     */
    operator fun invoke(
        input: String,
        existingNames: Set<String> = emptySet()
    ): CategoryNameValidationResult {
        if (input.isBlank()) {
            return CategoryNameValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.category_manager_enter_name)
            )
        }

        if (input in existingNames) {
            return CategoryNameValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.category_manager_name_already_used)
            )
        }

        if (input.length > MAX_LENGTH) {
            return CategoryNameValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(
                    R.string.category_manager_name_too_long,
                    MAX_LENGTH
                )
            )
        }

        return CategoryNameValidationResult(
            successful = true
        )
    }
}
