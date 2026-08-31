package dev.thomas_kiljanczyk.lyriccast.core.cast

import kotlinx.coroutines.flow.StateFlow

interface MessageTransport {
    val isBlanked: StateFlow<Boolean>

    /** Whether a Cast session is connected. Sends are silent no-ops without one. */
    val isConnected: StateFlow<Boolean>

    suspend fun sendContentMessage(message: String)

    suspend fun sendBlank(blanked: Boolean)

    suspend fun sendConfiguration(configuration: CastConfiguration)

    fun onSessionStarted()

    fun onSessionEnded()
}
