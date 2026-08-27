/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:27 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import java.util.UUID

data class Setlist(
    var id: UUID = UUIDv7.randomUUID(),
    var name: String,
    var presentation: List<Song>
)
