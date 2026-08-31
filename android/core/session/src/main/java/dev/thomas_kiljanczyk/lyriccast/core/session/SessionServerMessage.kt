package dev.thomas_kiljanczyk.lyriccast.core.session

import kotlinx.serialization.Serializable

@Serializable
data class SessionServerMessage(
    val command: SessionServerCommand
)
