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
    operator fun invoke(): Flow<ImmutableList<CategoryItem?>> {
        return categoriesRepository.getAllCategories()
            .map { categories ->
                val categoryItems = categories.map { CategoryItem(it) }.sorted()
                (listOf(null) + categoryItems).toImmutableList()
            }
    }
}
