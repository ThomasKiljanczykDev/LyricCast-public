package dev.thomas_kiljanczyk.lyriccast.core.model

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import java.util.UUID

data class Setlist(
    var id: UUID = UUIDv7.randomUUID(),
    var name: String,
    var presentation: List<Song>
)
