/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:34 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.models

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Category
import java.util.UUID

/**
 * Represents an item associated with a category.
 *
 * @property name The name of the category item.
 * @property id The unique identifier of the category item, generated using UUID version 7 by default.
 * @property color The ARGB color value associated with the category item, or null if not set.
 * @property isSelected Indicates whether this category item is currently selected.
 */
data class CategoryItem(
    val name: String,
    val id: UUID = UUIDv7.randomUUID(),
    val color: Int? = null,
    val isSelected: Boolean = false
) : Comparable<CategoryItem> {

    // Constructor for regular categories
    constructor(category: Category) : this(
        id = category.id,
        name = category.name,
        color = category.color
    )

    override fun compareTo(other: CategoryItem): Int {
        return name.compareTo(other.name)
    }

    companion object {
        fun fromCategory(category: Category, isSelected: Boolean = false): CategoryItem {
            return CategoryItem(
                id = category.id,
                name = category.name,
                color = category.color,
                isSelected = isSelected
            )
        }
    }
}
