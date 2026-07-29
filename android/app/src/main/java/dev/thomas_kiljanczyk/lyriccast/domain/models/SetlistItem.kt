/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:51 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:47 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.models

import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Setlist
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.util.UUID

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