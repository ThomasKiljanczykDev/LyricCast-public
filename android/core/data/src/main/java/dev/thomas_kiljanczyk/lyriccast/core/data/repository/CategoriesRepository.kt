/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    fun getAllCategories(): Flow<List<Category>>

    suspend fun upsertCategory(category: Category)

    suspend fun deleteCategories(categoryIds: Collection<UUID>)
}
