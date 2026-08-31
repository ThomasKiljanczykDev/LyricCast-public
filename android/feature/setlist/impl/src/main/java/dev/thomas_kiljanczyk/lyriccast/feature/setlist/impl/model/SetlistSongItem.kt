
package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import java.util.UUID

/**
 * This allows the same song to appear multiple times in a setlist.
 */
data class SetlistSongItem(
    val song: SongItem,
    val id: UUID = UUIDv7.randomUUID(),
    val isSelected: Boolean = false
)
