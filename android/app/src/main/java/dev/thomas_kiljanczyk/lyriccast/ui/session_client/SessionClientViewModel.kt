/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:55 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:54 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.session_client

import android.Manifest
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.NearbyConnectionLifecycleCallback
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.ShowLyricsContent
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.SimpleNearbyPayloadCallback
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionClientCommand
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionClientMessage
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionServerCommand
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionServerMessage
import javax.inject.Inject

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
    private val connectionsClient: ConnectionsClient,
    @param:ApplicationContext @field:ApplicationContext private val context: Context
) : ViewModel() {
    companion object {
        private const val TAG = "SessionClientModel"
    }

    private val _state = MutableSessionClientState()
    val state: SessionClientState get() = _state

    private var currentEndpointId: String? = null

    private fun handlePayload(payload: ByteArray?) {
        val payloadString = payload?.decodeToString() ?: return

        val message = SessionClientMessage.fromJson<ShowLyricsContent>(payloadString) ?: return

        when (message.command) {
            SessionClientCommand.SHOW_SLIDE -> {
                val content = message.content
                val newSlideContent = SlideContent(
                    content.songTitle, content.slideText, content.slideNumber, content.totalSlides
                )

                _state.currentSlide = newSlideContent
            }
        }
    }

    private inner class ClientConnectionLifecycleCallback : NearbyConnectionLifecycleCallback() {
        override fun onConnectionInitiated(
            endpointId: String, connectionInfo: ConnectionInfo
        ) {
            super.onConnectionInitiated(endpointId, connectionInfo)
            connectionsClient.acceptConnection(endpointId, SimpleNearbyPayloadCallback {
                handlePayload(it)
            })
        }

        override fun onConnectionResult(
            endpointId: String, connectionInfo: ConnectionInfo?, result: ConnectionResolution
        ) {
            if (result.status.isSuccess) {
                currentEndpointId = endpointId
                requestLatestSlide()

                _state.connectionState = ConnectionState.CONNECTED
            } else {
                currentEndpointId = null

                _state.connectionState = ConnectionState.FAILED
            }
        }

        override fun onDisconnected(endpointId: String, connectionInfo: ConnectionInfo?) {
            currentEndpointId = null
            connectionsClient.disconnectFromEndpoint(endpointId)

            _state.apply {
                connectionState = ConnectionState.DISCONNECTED
                currentSlide = SlideContent("", "", 0, 0)
            }
        }
    }

    fun requestLatestSlide() {
        currentEndpointId?.let { endpointId ->
            connectionsClient.sendPayload(
                endpointId, Payload.fromBytes(
                    SessionServerMessage(
                        SessionServerCommand.SEND_LATEST_SLIDE
                    ).toJson().toByteArray()
                )
            )
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH])
    fun startClient(endpointId: String) {
        try {
            val deviceName = Settings.Global.getString(
                context.contentResolver,
                Settings.Global.DEVICE_NAME
            ) ?: "Unknown Device"

            connectionsClient.requestConnection(
                deviceName, endpointId, ClientConnectionLifecycleCallback()
            )
        } catch (ex: SecurityException) {
            Log.e(TAG, "Failed to get device name", ex)
            _state.connectionState = ConnectionState.FAILED
        }
    }

    fun stopClient() {
        connectionsClient.stopAllEndpoints()
        Log.d(TAG, "Client disconnected")
    }
}
