/*
 * Created by Tomasz Kiljanczyk on 5/27/26, 9:55 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/27/26, 9:55 AM
 */

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

    /** Broadcasts [content] to every connected Cast device and session client. */
    suspend fun presentSlide(content: ShowLyricsContent)

    /** Sends [content] to a single session client, e.g. in response to a late-join request. */
    suspend fun presentSlideTo(endpointId: String, content: ShowLyricsContent)

    suspend fun setBlank(blanked: Boolean)

    suspend fun sendConfiguration(config: CastConfiguration)
}
