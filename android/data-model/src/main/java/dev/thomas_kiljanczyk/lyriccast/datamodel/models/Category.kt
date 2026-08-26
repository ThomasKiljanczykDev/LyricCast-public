/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.models

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import java.util.UUID

/**
 * @property color the ARGB color of the category, or null if not set
 */
data class Category(
    var name: String,
    var color: Int? = null,
    var id: UUID = UUIDv7.randomUUID()
) : Comparable<Category> {
    internal constructor(dto: CategoryDto) : this(dto.name, dto.color, UUIDv7.randomUUID())

    internal fun toDto(): CategoryDto {
        return CategoryDto(name, color)
    }

    override fun compareTo(other: Category): Int {
        return name.compareTo(other.name)
    }
}
