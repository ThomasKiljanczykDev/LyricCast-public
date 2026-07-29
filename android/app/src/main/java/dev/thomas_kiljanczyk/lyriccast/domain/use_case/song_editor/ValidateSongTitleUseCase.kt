/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 10:38 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:27 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.song_editor

import dev.thomas_kiljanczyk.lyriccast.shared.enums.NameValidationState
import javax.inject.Inject

/**
 * Use case for validating song titles.
 * Checks if a title is empty or already in use.
 */
class ValidateSongTitleUseCase @Inject constructor() {
    /**
     * Validates a song title against existing titles.
     *
     * @param songTitle The title to validate
     * @param existingTitles Set of already existing song titles
     * @param currentTitle The current title being edited (null for new songs)
     * @return NameValidationState indicating validation result
     */
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