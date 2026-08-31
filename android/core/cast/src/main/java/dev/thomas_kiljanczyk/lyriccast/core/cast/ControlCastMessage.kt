package dev.thomas_kiljanczyk.lyriccast.core.cast

import kotlinx.serialization.Serializable

@Serializable
data class ControlCastMessage<T>(
    val action: String,
    val value: T
)
