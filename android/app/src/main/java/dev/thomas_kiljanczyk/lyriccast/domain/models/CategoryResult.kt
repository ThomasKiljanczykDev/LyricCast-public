/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:31 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.models

import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Category
import java.util.UUID

/**
 * Result of saving a category operation.
 */
sealed class SaveCategoryResult {
    data class Success(val category: Category) : SaveCategoryResult()
    data class Error(val message: UiText) : SaveCategoryResult()
}

/**
 * Result of deleting categories operation.
 */
sealed class DeleteCategoriesResult {
    data class Success(val deletedCount: Int) : DeleteCategoriesResult()
    data class CategoriesInUse(val categoryIds: List<UUID>) : DeleteCategoriesResult()
    data class Error(val message: UiText) : DeleteCategoriesResult()
}
