/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:31 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.category_manager

import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Category
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.domain.models.SaveCategoryResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for saving (creating or updating) a category.
 */
class SaveCategoryUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    /**
     * Saves a category with the provided details.
     *
     * @param name The category name
     * @param colorValue The color value for the category
     * @param categoryId The existing category ID (null for new categories)
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        name: String,
        colorValue: Int,
        categoryId: UUID?
    ): SaveCategoryResult {
        return try {
            val category = Category(
                name = name.trim().uppercase(),
                color = colorValue,
                id = categoryId ?: UUIDv7.randomUUID()
            )

            categoriesRepository.upsertCategory(category)
            SaveCategoryResult.Success(category)
        } catch (e: Exception) {
            SaveCategoryResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.category_manager_save_failed)
            )
        }
    }
}
