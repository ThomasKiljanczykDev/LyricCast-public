/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:35 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.thomas_kiljanczyk.lyriccast.core.database.model.CategoryEntity
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategory(id: UUID): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategories(categories: List<CategoryEntity>)

    @Query("DELETE FROM categories WHERE id IN (:categoryIds)")
    suspend fun deleteCategories(categoryIds: Collection<UUID>)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}
