package dev.thomas_kiljanczyk.lyriccast.core.cast

import kotlinx.serialization.Serializable

@Serializable
data class TextCastMessage(
    val text: String
)
