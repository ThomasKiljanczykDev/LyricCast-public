package dev.thomas_kiljanczyk.lyriccast.core.cast

import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import dev.thomas_kiljanczyk.lyriccast.core.session.ShowLyricsContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Single entry point for the controls ViewModels to present a slide.
 *
 * Fans out to whichever transports are active — Cast and the Nearby session server — without
 * the caller knowing which is which. [isBlanked] is null when Cast is unavailable.
 */
interface SlidePresentationBus {
    val receivedPayload: Flow<ReceivedPayload>
    val isBlanked: StateFlow<Boolean>?

    /** False whenever nothing is being cast, so Cast-only controls can be disabled. */
    val isCastConnected: StateFlow<Boolean>

    /** Broadcasts [content] to every connected Cast device and session client. */
    suspend fun presentSlide(content: ShowLyricsContent)

    /** Sends [content] to a single session client, e.g. in response to a late-join request. */
    suspend fun presentSlideTo(endpointId: String, content: ShowLyricsContent)

    suspend fun setBlank(blanked: Boolean)

    suspend fun sendConfiguration(config: CastConfiguration)
}
