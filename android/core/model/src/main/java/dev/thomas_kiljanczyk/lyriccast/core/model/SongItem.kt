package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

/**
 * @param lyricsMap A map containing lyrics sections where the key is the section name and the value is the
 * section text.
 * @param presentation A list defining the order of lyrics sections for presentation purposes.
 */
data class SongItem(
    val id: UUID,
    val title: String,
    val lyricsMap: ImmutableMap<String, String>,
    val presentation: ImmutableList<String>,
    val category: CategoryItem?,
    val isSelected: Boolean = false
) : Comparable<SongItem> {

    val normalizedTitle: String = title.normalize()

    override fun compareTo(other: SongItem): Int {
        return title.compareTo(other.title)
    }

    companion object {
        fun fromSong(song: Song, isSelected: Boolean = false): SongItem {
            return SongItem(
                id = song.id,
                title = song.title,
                lyricsMap = song.lyricsMap.toImmutableMap(),
                presentation = song.presentation.toImmutableList(),
                category = song.category?.let { CategoryItem(it) },
                isSelected = isSelected
            )
        }
    }
}
