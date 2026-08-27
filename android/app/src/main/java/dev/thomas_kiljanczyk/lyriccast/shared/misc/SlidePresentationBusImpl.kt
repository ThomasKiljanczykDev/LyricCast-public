/*
 * Created by Tomasz Kiljanczyk on 6/6/26, 11:17 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 6/6/26, 11:11 PM
 */

package dev.thomas_kiljanczyk.lyriccast.shared.misc

import dev.thomas_kiljanczyk.lyriccast.core.cast.CastConfiguration
import dev.thomas_kiljanczyk.lyriccast.core.cast.MessageTransport
import dev.thomas_kiljanczyk.lyriccast.core.cast.SlidePresentationBus
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionClientCommand
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionClientMessage
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.core.session.ShowLyricsContent
import dev.thomas_kiljanczyk.lyriccast.core.session.encode
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Fans every slide-presentation operation out to whatever transports are active: the Cast text
 * channel and the GMS Nearby session server (when advertising). Owns the codec encoding and
 * debouncing that used to live in the standalone `LyricCastMessagingContext`.
 */
@OptIn(FlowPreview::class)
class SlidePresentationBusImpl(
    private val castMessageTransport: MessageTransport?,
    private val payloadTransport: PayloadTransport,
    private val codec: SessionMessageCodec,
    scope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher
) : SlidePresentationBus {

    private companion object {
        const val BROADCAST_DEBOUNCE_MS = 500L
    }

    override val receivedPayload: Flow<ReceivedPayload> = payloadTransport.receivedPayload

    override val isBlanked: StateFlow<Boolean>? = castMessageTransport?.isBlanked

    private val nearbyBroadcastFlow = MutableSharedFlow<ByteArray>()

    init {
        nearbyBroadcastFlow.debounce(BROADCAST_DEBOUNCE_MS.milliseconds).onEach { payload ->
            payloadTransport.broadcast(payload)
        }.flowOn(ioDispatcher).launchIn(scope)
    }

    override suspend fun presentSlide(content: ShowLyricsContent) {
        castMessageTransport?.sendContentMessage(content.slideText)

        if (payloadTransport.serverIsRunning.value) {
            val payload = codec.encode(
                SessionClientMessage(SessionClientCommand.SHOW_SLIDE, content)
            )
            nearbyBroadcastFlow.emit(payload)
        }
    }

    override suspend fun presentSlideTo(endpointId: String, content: ShowLyricsContent) {
        if (!payloadTransport.serverIsRunning.value) return

        val payload = codec.encode(
            SessionClientMessage(SessionClientCommand.SHOW_SLIDE, content)
        )
        payloadTransport.send(endpointId, payload)
    }

    override suspend fun setBlank(blanked: Boolean) {
        castMessageTransport?.sendBlank(blanked)
    }

    override suspend fun sendConfiguration(config: CastConfiguration) {
        castMessageTransport?.sendConfiguration(config)
    }
}
