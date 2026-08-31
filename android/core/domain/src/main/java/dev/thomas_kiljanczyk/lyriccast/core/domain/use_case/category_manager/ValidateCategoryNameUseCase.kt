package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager

import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryNameValidationResult
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import javax.inject.Inject

class ValidateCategoryNameUseCase @Inject constructor() {
    companion object {
        const val MAX_LENGTH = 30
    }

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
