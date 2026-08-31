package dev.thomas_kiljanczyk.lyriccast.core.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionClientMessage<T>(
    val command: SessionClientCommand,
    val content: T
)
