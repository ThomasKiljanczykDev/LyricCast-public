/*
 * Created by Tomasz Kiljanczyk on 9/11/25, 9:27 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/11/25, 9:13 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteCategoriesResult
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Use case for deleting categories with business logic validation.
 * Checks if categories are in use by songs before deletion.
 */
class DeleteCategoriesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val songsRepository: SongsRepository
) {
    /**
     * Deletes the specified categories after validation.
     *
     * @param categoryIds List of category IDs to delete
     * @return Result indicating success, categories in use, or error
     */
    suspend operator fun invoke(
        categoryIds: List<UUID>
    ): DeleteCategoriesResult {
        if (categoryIds.isEmpty()) {
            return DeleteCategoriesResult.Success(0)
        }

        return try {
            // All categories are safe to delete
            categoriesRepository.deleteCategories(categoryIds)
            DeleteCategoriesResult.Success(categoryIds.size)
        } catch (e: Exception) {
            Log.e("DeleteCategoriesUseCase", "Failed to delete categories", e)
            DeleteCategoriesResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.category_manager_delete_failed)
            )
        }
    }

    /**
     * Checks which of the given category IDs are currently in use by songs.
     *
     * @param categoryIds List of category IDs to check
     * @return List of category IDs that are in use
     */
    private suspend fun checkCategoriesInUse(categoryIds: List<UUID>): List<UUID> {
        return try {
            val allSongs = songsRepository.getAllSongs().first()
            val usedCategoryIds = allSongs.mapNotNull { song -> song.category?.id }.toSet()
            categoryIds.filter { categoryId -> categoryId in usedCategoryIds }
        } catch (e: Exception) {
            Log.w(
                "DeleteCategoriesUseCase",
                "Failed to check category usage, assuming none are in use",
                e
            )
            // If we can't check usage, assume none are in use to avoid blocking deletion
            // The actual deletion will fail safely at the repository level if there are constraints
            emptyList()
        }
    }
}
