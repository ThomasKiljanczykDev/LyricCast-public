package dev.thomas_kiljanczyk.lyriccast.core.model

import java.util.UUID

sealed class SaveCategoryResult {
    data class Success(val category: Category) : SaveCategoryResult()
    data class Error(val message: UiText) : SaveCategoryResult()
}

sealed class DeleteCategoriesResult {
    data class Success(val deletedCount: Int) : DeleteCategoriesResult()
    data class CategoriesInUse(val categoryIds: List<UUID>) : DeleteCategoriesResult()
    data class Error(val message: UiText) : DeleteCategoriesResult()
}
