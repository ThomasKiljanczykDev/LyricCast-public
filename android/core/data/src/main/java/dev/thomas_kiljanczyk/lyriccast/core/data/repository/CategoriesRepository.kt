package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    fun getAllCategories(): Flow<List<Category>>

    suspend fun upsertCategory(category: Category)

    suspend fun deleteCategories(categoryIds: Collection<UUID>)
}
