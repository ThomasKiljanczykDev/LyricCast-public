/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 10:24 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:21 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.category_manager

import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving category names.
 * Optimized to return only the category names instead of full category objects.
 */
class GetCategoryNamesUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    /**
     * Returns a flow of category names.
     *
     * @return Flow emitting a set of all category names
     */
    operator fun invoke(): Flow<Set<String>> =
        categoriesRepository.getAllCategories()
            .map { categories -> categories.map { it.name }.toSet() }
}