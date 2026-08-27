/*
 * Created by Tomasz Kiljanczyk on 5/27/26, 11:30 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/27/26, 11:30 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.sync

import android.os.Build
import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.nearby.ClientConnectionEvent
import dev.thomas_kiljanczyk.lyriccast.core.nearby.DiscoveryState
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.SyncTimeoutException
import dev.thomas_kiljanczyk.lyriccast.core.nearby.TransportConfig
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.core.session.decode
import dev.thomas_kiljanczyk.lyriccast.core.sync.use_case.ReceiveDataViaNearbyUseCase
import dev.thomas_kiljanczyk.lyriccast.core.sync.use_case.SendDataViaNearbyUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents a discovered sync device
 */
data class DiscoveredSyncDevice(
    val endpointId: String,
    val endpointName: String
)

/**
 * Single source of truth for sync session state
 */
data class SyncSessionState(
    val isSending: Boolean = false,
    val isReceiving: Boolean = false,
    val discoveredDevices: List<DiscoveredSyncDevice> = emptyList(),
    val selectedDeviceId: String? = null,
    val progressMessage: UiText? = null,
    val isProgressComplete: Boolean = false,
    val hasError: Boolean = false,
    val hasDiscoveryError: Boolean = false
) {
    val showProgressDialog: Boolean
        get() = progressMessage != null

    val isActive: Boolean
        get() = isSending || isReceiving
}

/**
 * Manages sync sessions for GMS Nearby import/export.
 * Provides a single source of truth for sync state and handles all sync operations.
 */
@Singleton
class SyncSessionManager @Inject constructor(
    private val payloadTransport: PayloadTransport,
    private val discoveryStream: DeviceDiscoveryStream,
    private val sendDataViaNearbyUseCase: SendDataViaNearbyUseCase,
    private val receiveDataViaNearbyUseCase: ReceiveDataViaNearbyUseCase,
    private val codec: SessionMessageCodec
) {
    companion object {
        private const val TAG = "SyncSessionManager"
    }

    private val _state = MutableStateFlow(SyncSessionState())
    val state: StateFlow<SyncSessionState> = _state.asStateFlow()

    private var syncSendJob: Job? = null
    private var discoveryJob: Job? = null
    private var discoveryErrorJob: Job? = null
    private var clientEventJob: Job? = null
    private var receivedPayloadJob: Job? = null
    private var managerScope: CoroutineScope? = null

    /**
     * Initialize the manager with a scope for coroutine operations.
     * Call this from ViewModel's init block.
     */
    fun initialize(scope: CoroutineScope) {
        discoveryJob?.cancel()
        discoveryErrorJob?.cancel()
        clientEventJob?.cancel()
        receivedPayloadJob?.cancel()
        managerScope = scope

        discoveryStream.subscribe(scope)

        discoveryJob = discoveryStream.devices
            .onEach { devices ->
                _state.update { it.copy(discoveredDevices = devices) }
            }
            .launchIn(scope)

        // Surface discovery errors while we're actively receiving without a selected device
        discoveryErrorJob = payloadTransport.discoveryState
            .onEach { info ->
                if (info.state == DiscoveryState.FAILED) {
                    _state.update { currentState ->
                        if (currentState.isReceiving && currentState.selectedDeviceId == null) {
                            currentState.copy(hasDiscoveryError = true)
                        } else {
                            currentState
                        }
                    }
                }
            }
            .launchIn(scope)

        clientEventJob = payloadTransport.clientConnectionEvents
            .onEach { event -> handleClientConnectionEvent(event) }
            .launchIn(scope)

        receivedPayloadJob = payloadTransport.receivedPayload
            .onEach { payload -> handleSyncPayload(payload.payload) }
            .launchIn(scope)
    }

    fun startSending() {
        val scope = managerScope ?: run {
            Log.e(TAG, "Manager not initialized")
            return
        }

        _state.update {
            it.copy(
                isSending = true,
                progressMessage = UiText.StringResource(R.string.sync_starting_broadcast),
                isProgressComplete = false,
                hasError = false
            )
        }

        val deviceName = Build.MODEL ?: "Android Device"
        payloadTransport.startServer(deviceName, TransportConfig.Sync)

        syncSendJob = scope.launch {
            try {
                sendDataViaNearbyUseCase(deviceName).collect { messageId ->
                    _state.update { it.copy(progressMessage = UiText.StringResource(messageId)) }
                }
                _state.update { it.copy(isProgressComplete = true) }
            } catch (e: SyncTimeoutException) {
                Log.e(TAG, "Sync timeout", e)
                _state.update {
                    it.copy(
                        progressMessage = UiText.StringResource(R.string.sync_error_expired),
                        hasError = true,
                        isProgressComplete = true
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending sync message", e)
                _state.update {
                    it.copy(
                        progressMessage = UiText.StringResource(R.string.sync_error_send_failed),
                        hasError = true,
                        isProgressComplete = true
                    )
                }
            } finally {
                payloadTransport.stopServer()
                _state.update { it.copy(isSending = false) }
                syncSendJob = null
            }
        }
    }

    fun stopSending() {
        syncSendJob?.cancel()
        syncSendJob = null
        payloadTransport.stopServer()
        discoveryStream.clear()
        _state.update {
            SyncSessionState()
        }
    }

    fun startReceiving() {
        discoveryStream.clear()
        _state.update {
            it.copy(
                isReceiving = true,
                hasDiscoveryError = false,
                selectedDeviceId = null
            )
        }

        payloadTransport.startDiscovery(TransportConfig.Sync)
    }

    fun stopReceiving() {
        payloadTransport.stopDiscovery()

        _state.update { currentState ->
            currentState.copy(isReceiving = false)
        }
        if (_state.value.selectedDeviceId == null) {
            discoveryStream.clear()
        }
    }

    fun selectDevice(endpointId: String) {
        val endpointName = _state.value.discoveredDevices
            .firstOrNull { it.endpointId == endpointId }
            ?.endpointName
            ?: run {
                Log.e(TAG, "Unknown endpoint: $endpointId")
                return
            }

        _state.update {
            it.copy(
                selectedDeviceId = endpointId,
                progressMessage = UiText.StringResource(R.string.sync_connecting),
                isProgressComplete = false,
                hasError = false
            )
        }

        payloadTransport.stopDiscovery()
        Log.d(TAG, "Requesting connection to $endpointName ($endpointId)")

        val deviceName = Build.MODEL ?: "Android Device"
        try {
            payloadTransport.startClient(endpointId, deviceName, TransportConfig.Sync)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to request connection to $endpointId", e)
            _state.update {
                it.copy(
                    progressMessage = UiText.DynamicString("Failed to request connection: ${e.message}"),
                    hasError = true,
                    isProgressComplete = true
                )
            }
            cleanupReceiveState()
        }
    }

    fun setDiscoveryError() {
        _state.update { it.copy(hasDiscoveryError = true) }
    }

    fun dismissProgress() {
        _state.update {
            it.copy(
                progressMessage = null,
                isProgressComplete = false,
                hasError = false
            )
        }
    }

    private fun handleClientConnectionEvent(event: ClientConnectionEvent) {
        when (event) {
            is ClientConnectionEvent.Initiated -> {
                Log.d(TAG, "Sync connection initiated from ${event.deviceName}")
            }

            is ClientConnectionEvent.Result -> {
                if (!event.success) {
                    Log.e(TAG, "Sync connection failed for ${event.endpointId}")
                    val current = _state.value
                    if (current.selectedDeviceId == event.endpointId) {
                        _state.update {
                            it.copy(
                                progressMessage = UiText.StringResource(R.string.sync_error_send_failed),
                                hasError = true,
                                isProgressComplete = true
                            )
                        }
                        cleanupReceiveState()
                    }
                } else {
                    Log.d(TAG, "Sync connection successful with ${event.endpointId}")
                    if (_state.value.selectedDeviceId == event.endpointId) {
                        _state.update {
                            it.copy(progressMessage = UiText.StringResource(R.string.sync_connected_waiting_data))
                        }
                    }
                }
            }

            is ClientConnectionEvent.Disconnected -> {
                Log.d(TAG, "Disconnected from ${event.endpointId}")
                if (_state.value.isReceiving) {
                    managerScope?.launch { cleanupReceiveState() }
                }
            }
        }
    }

    private fun cleanupReceiveState() {
        payloadTransport.stopAllEndpoints()
        discoveryStream.clear()
        _state.update {
            it.copy(
                selectedDeviceId = null,
                isReceiving = false
            )
        }
    }

    private suspend fun handleSyncPayload(payload: ByteArray) {
        if (!_state.value.isReceiving || _state.value.selectedDeviceId == null) {
            return
        }

        try {
            val syncMessage = codec.decode<GmsSyncMessage>(payload)
            handleReceivedSyncMessage(syncMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Error deserializing sync message", e)
            _state.update {
                it.copy(
                    progressMessage = UiText.DynamicString("Error receiving sync data: ${e.message}"),
                    hasError = true,
                    isProgressComplete = true
                )
            }
            cleanupReceiveState()
        }
    }

    private suspend fun handleReceivedSyncMessage(syncMessage: GmsSyncMessage) {
        try {
            val importOptions = ImportOptions(
                deleteAll = false,
                replaceOnConflict = true
            )

            receiveDataViaNearbyUseCase(syncMessage, importOptions).collect { uiText ->
                _state.update { it.copy(progressMessage = uiText) }
            }

            _state.update { it.copy(isProgressComplete = true) }
        } catch (e: Exception) {
            Log.e(TAG, "Error importing data", e)
            _state.update {
                it.copy(
                    progressMessage = UiText.StringResource(R.string.sync_error_import_failed),
                    isProgressComplete = true
                )
            }
        } finally {
            discoveryStream.clear()
            _state.update {
                it.copy(
                    selectedDeviceId = null,
                    isReceiving = false
                )
            }
        }
    }
}
