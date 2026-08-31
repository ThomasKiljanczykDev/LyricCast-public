package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import java.util.UUID

/**
 * @property id The unique identifier of the category item, generated using UUID version 7 by default.
 */
data class CategoryItem(
    val name: String,
    val id: UUID = UUIDv7.randomUUID(),
    val color: Int? = null,
    val isSelected: Boolean = false
) : Comparable<CategoryItem> {

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
