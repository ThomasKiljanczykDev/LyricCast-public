package dev.thomas_kiljanczyk.lyriccast.core.nearby_test.fake

import dev.thomas_kiljanczyk.lyriccast.core.nearby.AdvertisingState
import dev.thomas_kiljanczyk.lyriccast.core.nearby.AdvertisingStateInfo
import dev.thomas_kiljanczyk.lyriccast.core.nearby.ClientConnectionEvent
import dev.thomas_kiljanczyk.lyriccast.core.nearby.DeviceConnectionInfo
import dev.thomas_kiljanczyk.lyriccast.core.nearby.DiscoveredDevice
import dev.thomas_kiljanczyk.lyriccast.core.nearby.DiscoveryState
import dev.thomas_kiljanczyk.lyriccast.core.nearby.DiscoveryStateInfo
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.TransportConfig
import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * In-memory [PayloadTransport] for tests. `send`/`broadcast` push to [receivedPayload]
 * so a single-process round-trip can be exercised without GMS Nearby.
 */
class FakePayloadTransport : PayloadTransport {
    private val _serverIsRunning = MutableStateFlow(false)
    override val serverIsRunning: StateFlow<Boolean> = _serverIsRunning

    private val _deviceConnectionInfo = MutableSharedFlow<DeviceConnectionInfo>(replay = 1)
    override val deviceConnectionInfo: Flow<DeviceConnectionInfo> = _deviceConnectionInfo

    private val _advertisingState = MutableSharedFlow<AdvertisingStateInfo>(replay = 1)
    override val advertisingState: Flow<AdvertisingStateInfo> = _advertisingState

    private val _clientConnectionEvents = MutableSharedFlow<ClientConnectionEvent>(replay = 1)
    override val clientConnectionEvents: Flow<ClientConnectionEvent> = _clientConnectionEvents

    private val _receivedPayload = MutableSharedFlow<ReceivedPayload>(replay = 1)
    override val receivedPayload: Flow<ReceivedPayload> = _receivedPayload

    private val _discoveryState = MutableStateFlow(
        DiscoveryStateInfo(DiscoveryState.NOT_DISCOVERING, null)
    )
    override val discoveryState: StateFlow<DiscoveryStateInfo> = _discoveryState

    private val _discoveredDevices = MutableSharedFlow<DiscoveredDevice>(replay = 1)
    override val discoveredDevices: Flow<DiscoveredDevice> = _discoveredDevices

    private val connectedEndpoints = mutableSetOf<String>()

    override fun startServer(deviceName: String, config: TransportConfig) {
        _serverIsRunning.value = true
        _advertisingState.tryEmit(AdvertisingStateInfo(AdvertisingState.ADVERTISING, null))
    }

    override fun stopServer() {
        _serverIsRunning.value = false
        connectedEndpoints.clear()
        _advertisingState.tryEmit(AdvertisingStateInfo(AdvertisingState.NOT_ADVERTISING, null))
    }

    override fun broadcast(payload: ByteArray) {
        connectedEndpoints.forEach { endpointId ->
            _receivedPayload.tryEmit(ReceivedPayload(endpointId, payload))
        }
    }

    override fun startClient(endpointId: String, deviceName: String, config: TransportConfig) {
        connectedEndpoints.add(endpointId)
        _clientConnectionEvents.tryEmit(ClientConnectionEvent.Initiated(endpointId, deviceName))
        _clientConnectionEvents.tryEmit(ClientConnectionEvent.Result(endpointId, true))
    }

    override fun disconnectFromEndpoint(endpointId: String) {
        if (connectedEndpoints.remove(endpointId)) {
            _clientConnectionEvents.tryEmit(ClientConnectionEvent.Disconnected(endpointId))
        }
    }

    override fun stopAllEndpoints() {
        val snapshot = connectedEndpoints.toList()
        connectedEndpoints.clear()
        snapshot.forEach { _clientConnectionEvents.tryEmit(ClientConnectionEvent.Disconnected(it)) }
    }

    override fun send(endpointId: String, payload: ByteArray) {
        _receivedPayload.tryEmit(ReceivedPayload(endpointId, payload))
    }

    override fun startDiscovery(config: TransportConfig) {
        _discoveryState.value = DiscoveryStateInfo(DiscoveryState.DISCOVERING, null)
    }

    override fun stopDiscovery() {
        _discoveryState.value = DiscoveryStateInfo(DiscoveryState.NOT_DISCOVERING, null)
    }

    fun emitDiscovered(endpointId: String, endpointName: String) {
        _discoveredDevices.tryEmit(DiscoveredDevice(endpointId, endpointName))
    }
}
