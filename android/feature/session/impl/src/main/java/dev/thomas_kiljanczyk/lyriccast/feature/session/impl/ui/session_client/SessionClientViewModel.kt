/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:55 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:54 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client

import android.Manifest
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.thomas_kiljanczyk.lyriccast.core.nearby.ClientConnectionEvent
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.TransportConfig
import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionClientCommand
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionClientMessage
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionServerCommand
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionServerMessage
import dev.thomas_kiljanczyk.lyriccast.core.session.ShowLyricsContent
import dev.thomas_kiljanczyk.lyriccast.core.session.decodeOrNull
import dev.thomas_kiljanczyk.lyriccast.core.session.encode
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class ConnectionState {
    UNKNOWN, DISCONNECTED, CONNECTED, FAILED
}

data class SlideContent(
    val songTitle: String, val slideText: String, val slideNumber: Int, val totalSlides: Int
)

interface SessionClientState {
    val currentSlide: SlideContent
    val connectionState: ConnectionState
}

class MutableSessionClientState : SessionClientState {
    override var currentSlide by mutableStateOf(SlideContent("", "", 0, 0))
    override var connectionState by mutableStateOf(ConnectionState.UNKNOWN)
}

@HiltViewModel
class SessionClientViewModel @Inject constructor(
    private val payloadTransport: PayloadTransport,
    private val codec: SessionMessageCodec,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    companion object {
        private const val TAG = "SessionClientModel"
    }

    private val _state = MutableSessionClientState()
    val state: SessionClientState get() = _state

    private var currentEndpointId: String? = null

    init {
        payloadTransport.clientConnectionEvents
            .onEach(::handleConnectionEvent)
            .launchIn(viewModelScope)

        payloadTransport.receivedPayload
            .onEach { handlePayload(it) }
            .launchIn(viewModelScope)
    }

    private fun handleConnectionEvent(event: ClientConnectionEvent) {
        when (event) {
            is ClientConnectionEvent.Result -> {
                if (event.success) {
                    currentEndpointId = event.endpointId
                    requestLatestSlide()
                    _state.connectionState = ConnectionState.CONNECTED
                } else {
                    currentEndpointId = null
                    _state.connectionState = ConnectionState.FAILED
                }
            }

            is ClientConnectionEvent.Disconnected -> {
                currentEndpointId = null
                _state.apply {
                    connectionState = ConnectionState.DISCONNECTED
                    currentSlide = SlideContent("", "", 0, 0)
                }
            }

            is ClientConnectionEvent.Initiated -> Unit
        }
    }

    private suspend fun handlePayload(receivedPayload: ReceivedPayload) {
        Log.d(TAG, "Received payload from ${receivedPayload.endpointId}")

        val message =
            codec.decodeOrNull<SessionClientMessage<ShowLyricsContent>>(receivedPayload.payload)
                ?: return

        when (message.command) {
            SessionClientCommand.SHOW_SLIDE -> {
                val content = message.content
                _state.currentSlide = SlideContent(
                    content.songTitle, content.slideText, content.slideNumber, content.totalSlides
                )
            }
        }
    }

    fun requestLatestSlide() {
        val endpointId = currentEndpointId ?: return
        viewModelScope.launch {
            val payload = codec.encode(SessionServerMessage(SessionServerCommand.SEND_LATEST_SLIDE))
            payloadTransport.send(endpointId, payload)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH])
    fun startClient(endpointId: String) {
        try {
            val deviceName = Settings.Global.getString(
                context.contentResolver,
                Settings.Global.DEVICE_NAME
            ) ?: "Unknown Device"

            payloadTransport.startClient(endpointId, deviceName, TransportConfig.Session)
        } catch (ex: SecurityException) {
            Log.e(TAG, "Failed to get device name", ex)
            _state.connectionState = ConnectionState.FAILED
        }
    }

    fun stopClient() {
        payloadTransport.stopAllEndpoints()
        Log.d(TAG, "Client disconnected")
    }
}
