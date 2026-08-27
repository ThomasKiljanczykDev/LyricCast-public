/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val color: Int?
) {
    constructor(category: Category) : this(
        id = category.id,
        name = category.name,
        color = category.color
    )

    fun toGenericModel(): Category {
        return Category(
            id = id,
            name = name,
            color = color
        )
    }
}
