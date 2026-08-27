/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 11:00 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:58 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.shared

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving categories with a null option for "no category" selection.
 * Commonly used in dropdowns and selection lists where "no category" is an option.
 */
class GetCategoriesWithNullOptionUseCase @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) {
    /**
     * Returns a flow of categories with null as the first option.
     *
     * @return Flow emitting an immutable list with null + sorted CategoryItems
     */
    operator fun invoke(): Flow<ImmutableList<CategoryItem?>> {
        return categoriesRepository.getAllCategories()
            .map { categories ->
                val categoryItems = categories.map { CategoryItem(it) }.sorted()
                (listOf(null) + categoryItems).toImmutableList()
            }
    }
}
