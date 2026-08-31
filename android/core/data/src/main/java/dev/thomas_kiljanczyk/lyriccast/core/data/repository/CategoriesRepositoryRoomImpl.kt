package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.CategoryDao
import dev.thomas_kiljanczyk.lyriccast.core.database.model.CategoryEntity
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class CategoriesRepositoryRoomImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : CategoriesRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { categories -> categories.map { it.toGenericModel() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun upsertCategory(category: Category) {
        withContext(ioDispatcher) {
            categoryDao.upsertCategory(CategoryEntity(category))
        }
    }

    override suspend fun deleteCategories(categoryIds: Collection<UUID>) {
        withContext(ioDispatcher) {
            categoryDao.deleteCategories(categoryIds)
        }
    }
}
