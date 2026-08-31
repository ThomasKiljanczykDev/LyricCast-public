package dev.thomas_kiljanczyk.lyriccast.core.model

sealed class SaveSetlistResult {
    data class Success(val setlist: Setlist) : SaveSetlistResult()
    data class ValidationError(val message: UiText) : SaveSetlistResult()
    data class Error(val message: UiText) : SaveSetlistResult()
}

sealed class DeleteSetlistsResult {
    data class Success(val deletedCount: Int) : DeleteSetlistsResult()
    data class Error(val message: UiText) : DeleteSetlistsResult()
}

data class CategoryNameValidationResult(
    val successful: Boolean,
    val errorMessage: UiText? = null
)
