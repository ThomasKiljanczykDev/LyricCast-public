package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import java.util.UUID

/**
 * @property color the ARGB color of the category, or null if not set
 */
data class Category(
    var name: String,
    var color: Int? = null,
    var id: UUID = UUIDv7.randomUUID()
) : Comparable<Category> {
    override fun compareTo(other: Category): Int {
        return name.compareTo(other.name)
    }
}
