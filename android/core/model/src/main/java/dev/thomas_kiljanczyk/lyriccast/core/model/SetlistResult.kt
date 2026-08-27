/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:33 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.model

/**
 * Result of saving a setlist operation.
 */
sealed class SaveSetlistResult {
    data class Success(val setlist: Setlist) : SaveSetlistResult()
    data class ValidationError(val message: UiText) : SaveSetlistResult()
    data class Error(val message: UiText) : SaveSetlistResult()
}

/**
 * Result of deleting setlists operation.
 */
sealed class DeleteSetlistsResult {
    data class Success(val deletedCount: Int) : DeleteSetlistsResult()
    data class Error(val message: UiText) : DeleteSetlistsResult()
}

data class CategoryNameValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null
)
