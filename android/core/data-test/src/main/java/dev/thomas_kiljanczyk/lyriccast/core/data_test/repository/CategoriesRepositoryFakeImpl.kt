package dev.thomas_kiljanczyk.lyriccast.core.data_test.repository

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CategoriesRepositoryFakeImpl @Inject constructor() : CategoriesRepository {
    private val categories = mutableListOf<Category>()
    private val categoryFlow = MutableStateFlow(categories.toList())

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryFlow
    }

    override suspend fun upsertCategory(category: Category) {
        val existingCategory = categories.find { it.id == category.id }
        if (existingCategory != null) {
            categories.remove(existingCategory)
        }

        categories += category
        categoryFlow.emit(categories.toList())
    }

    override suspend fun deleteCategories(categoryIds: Collection<UUID>) {
        categories.removeIf { it.id in categoryIds }
        categoryFlow.emit(categories.toList())
    }
}
