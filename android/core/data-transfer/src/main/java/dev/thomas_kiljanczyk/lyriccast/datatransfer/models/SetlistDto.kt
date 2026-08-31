package dev.thomas_kiljanczyk.lyriccast.datatransfer.models

import kotlinx.serialization.Serializable

@Serializable
data class SetlistDto(
    val name: String,
    val songs: List<String>
)
