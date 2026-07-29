/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:19 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.impl.room

import dev.thomas_kiljanczyk.lyriccast.datamodel.dao.CategoryDao
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Category
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.CategoryEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.CategoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

internal class CategoriesRepositoryRoomImpl(
    private val categoryDao: CategoryDao
) : CategoriesRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
            .map { categories -> categories.map { it.toGenericModel() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun upsertCategory(category: Category) {
        withContext(Dispatchers.IO) {
            categoryDao.upsertCategory(CategoryEntity(category))
        }
    }

    override suspend fun deleteCategories(categoryIds: Collection<UUID>) {
        withContext(Dispatchers.IO) {
            categoryDao.deleteCategories(categoryIds)
        }
    }
}