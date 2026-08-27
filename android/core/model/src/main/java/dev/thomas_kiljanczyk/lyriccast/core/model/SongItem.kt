/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:08 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

/**
 * Represents a song item, including metadata, lyrics, and presentation details.
 *
 * @param id The unique identifier for the song.
 * @param title The title of the song.
 * @param lyricsMap A map containing lyrics sections where the key is the section name and the value is the
 * section text.
 * @param presentation A list defining the order of lyrics sections for presentation purposes.
 * @param category The category associated with this song, if any.
 * @param isSelected Indicates whether this song item is currently selected.
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
