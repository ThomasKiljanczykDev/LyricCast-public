
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

/** A single entry of the presenter's running order, shown read-only on the client. */
data class SetlistSongInfo(val id: String, val title: String)

/**
 * The presenter's setlist, or null when a single song is being presented. Mirrors
 * [dev.thomas_kiljanczyk.lyriccast.core.session.SetlistContent] at the UI layer, the same way
 * [SlideContent] mirrors the slide payload.
 */
data class SetlistInfo(
    val songs: List<SetlistSongInfo>,
    val currentSongIndex: Int
)

interface SessionClientState {
    val currentSlide: SlideContent
    val connectionState: ConnectionState
    val setlist: SetlistInfo?
}

class MutableSessionClientState : SessionClientState {
    override var currentSlide by mutableStateOf(SlideContent("", "", 0, 0))
    override var connectionState by mutableStateOf(ConnectionState.UNKNOWN)
    override var setlist by mutableStateOf<SetlistInfo?>(null)
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

    val state: SessionClientState
        field = MutableSessionClientState()

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
                    state.connectionState = ConnectionState.CONNECTED
                } else {
                    currentEndpointId = null
                    state.connectionState = ConnectionState.FAILED
                }
            }

            is ClientConnectionEvent.Disconnected -> {
                currentEndpointId = null
                state.apply {
                    connectionState = ConnectionState.DISCONNECTED
                    currentSlide = SlideContent("", "", 0, 0)
                    setlist = null
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
                state.currentSlide = SlideContent(
                    content.songTitle, content.slideText, content.slideNumber, content.totalSlides
                )
                state.setlist = content.setlist?.let { setlist ->
                    SetlistInfo(
                        songs = setlist.songs.map { SetlistSongInfo(it.id, it.title) },
                        currentSongIndex = setlist.currentSongIndex
                    )
                }
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
            state.connectionState = ConnectionState.FAILED
        }
    }

    fun stopClient() {
        payloadTransport.stopAllEndpoints()
        Log.d(TAG, "Client disconnected")
    }
}
