package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

data class SetlistItem(
    val id: UUID,
    val name: String,
    val presentation: ImmutableList<SongItem>,
    val isSelected: Boolean = false
) : Comparable<SetlistItem> {

    val normalizedName = name.normalize()

    override fun compareTo(other: SetlistItem): Int {
        return name.compareTo(other.name)
    }

    companion object {
        fun fromSetlist(setlist: Setlist, isSelected: Boolean = false): SetlistItem {
            return SetlistItem(
                id = setlist.id,
                name = setlist.name,
                presentation = setlist.presentation.map { SongItem.fromSong(it) }.toImmutableList(),
                isSelected = isSelected
            )
        }
    }
}
