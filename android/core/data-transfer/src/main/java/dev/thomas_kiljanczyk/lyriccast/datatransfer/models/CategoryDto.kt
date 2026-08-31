package dev.thomas_kiljanczyk.lyriccast.datatransfer.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(val name: String, val color: Int?)
