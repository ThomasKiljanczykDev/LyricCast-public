package dev.thomas_kiljanczyk.lyriccast.core.cast

import kotlinx.serialization.Serializable

@Serializable
data class CastConfiguration(
    val backgroundColor: String,
    val fontColor: String,
    val maxFontSize: Int
)
