/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import java.util.UUID

/**
 * Represents a song item in a setlist with its own unique identifier.
 * This allows the same song to appear multiple times in a setlist.
 */
data class SetlistSongItem(
    val song: SongItem,
    val id: UUID = UUIDv7.randomUUID(),
    val isSelected: Boolean = false
)
