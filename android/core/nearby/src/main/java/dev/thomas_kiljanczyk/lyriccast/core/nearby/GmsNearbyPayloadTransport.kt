package dev.thomas_kiljanczyk.lyriccast.core.nearby

import android.util.Log
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Singleton
internal class GmsNearbyPayloadTransport @Inject constructor(
    private val connectionsClient: ConnectionsClient,
    @param:ApplicationScope private val scope: CoroutineScope
) : PayloadTransport {
    companion object {
        private const val TAG = "GmsNearbyPayloadTransport"
    }

    private val _serverIsRunning = MutableStateFlow(false)
    override val serverIsRunning: StateFlow<Boolean> = _serverIsRunning

    private val _deviceConnectionInfo = MutableSharedFlow<DeviceConnectionInfo>()
    override val deviceConnectionInfo: Flow<DeviceConnectionInfo> = _deviceConnectionInfo

    private val _advertisingState = MutableSharedFlow<AdvertisingStateInfo>()
    override val advertisingState: Flow<AdvertisingStateInfo> = _advertisingState

    private val _clientConnectionEvents = MutableSharedFlow<ClientConnectionEvent>()
    override val clientConnectionEvents: Flow<ClientConnectionEvent> = _clientConnectionEvents

    private val _receivedPayload = MutableSharedFlow<ReceivedPayload>()
    override val receivedPayload: Flow<ReceivedPayload> = _receivedPayload

    private val _discoveryState = MutableStateFlow(
        DiscoveryStateInfo(DiscoveryState.NOT_DISCOVERING, null)
    )
    override val discoveryState: StateFlow<DiscoveryStateInfo> = _discoveryState

    private val _discoveredDevices = MutableSharedFlow<DiscoveredDevice>(replay = 1)
    override val discoveredDevices: Flow<DiscoveredDevice> = _discoveredDevices

    private val connectedEndpointIds = mutableSetOf<String>()
    private val discoveredEndpoints = ConcurrentHashMap<String, String>()
    private val serverConnectionLifecycleCallback = ServerConnectionLifecycleCallback()
    private val clientConnectionLifecycleCallback = ClientConnectionLifecycleCallback()
    private val discoveryCallback = DiscoveryCallback()

    private val payloadCallback: (String, ByteArray?) -> Unit = { endpointId, payloadBytes ->
        if (payloadBytes != null) {
            scope.launch {
                _receivedPayload.emit(ReceivedPayload(endpointId, payloadBytes))
            }
        }
    }

    private inner class ServerConnectionLifecycleCallback : NearbyConnectionLifecycleCallback() {
        override fun onConnectionInitiated(
            endpointId: String, connectionInfo: ConnectionInfo
        ) {
            super.onConnectionInitiated(endpointId, connectionInfo)
            connectionsClient.acceptConnection(endpointId, SimpleNearbyPayloadCallback {
                payloadCallback(endpointId, it)
            })
        }

        override fun onConnectionResult(
            endpointId: String, connectionInfo: ConnectionInfo?, result: ConnectionResolution
        ) {
            if (result.status.isSuccess) {
                connectedEndpointIds.add(endpointId)

                if (connectionInfo != null) {
                    scope.launch {
                        _deviceConnectionInfo.emit(
                            DeviceConnectionInfo(
                                connectionInfo.endpointName, ConnectionState.CONNECTED
                            )
                        )
                    }
                }
            } else if (connectionInfo != null) {
                scope.launch {
                    _deviceConnectionInfo.emit(
                        DeviceConnectionInfo(
                            connectionInfo.endpointName, ConnectionState.FAILED
                        )
                    )
                }
            }
        }

        override fun onDisconnected(endpointId: String, connectionInfo: ConnectionInfo?) {
            if (connectionInfo != null) {
                scope.launch {
                    _deviceConnectionInfo.emit(
                        DeviceConnectionInfo(
                            connectionInfo.endpointName, ConnectionState.DISCONNECTED
                        )
                    )
                }
            }
            connectedEndpointIds.remove(endpointId)
        }
    }

    private inner class ClientConnectionLifecycleCallback : NearbyConnectionLifecycleCallback() {
        override fun onConnectionInitiated(
            endpointId: String, connectionInfo: ConnectionInfo
        ) {
            super.onConnectionInitiated(endpointId, connectionInfo)
            connectionsClient.acceptConnection(endpointId, SimpleNearbyPayloadCallback {
                payloadCallback(endpointId, it)
            })
            scope.launch {
                _clientConnectionEvents.emit(
                    ClientConnectionEvent.Initiated(endpointId, connectionInfo.endpointName)
                )
            }
        }

        override fun onConnectionResult(
            endpointId: String, connectionInfo: ConnectionInfo?, result: ConnectionResolution
        ) {
            if (result.status.isSuccess) {
                connectedEndpointIds.add(endpointId)
            }
            scope.launch {
                _clientConnectionEvents.emit(
                    ClientConnectionEvent.Result(endpointId, result.status.isSuccess)
                )
            }
        }

        override fun onDisconnected(endpointId: String, connectionInfo: ConnectionInfo?) {
            connectedEndpointIds.remove(endpointId)
            scope.launch {
                _clientConnectionEvents.emit(ClientConnectionEvent.Disconnected(endpointId))
            }
        }
    }

    private inner class DiscoveryCallback : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Endpoint discovered: $endpointId - ${info.endpointName}")
            discoveredEndpoints[endpointId] = info.endpointName
            scope.launch {
                _discoveredDevices.emit(DiscoveredDevice(endpointId, info.endpointName))
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "Endpoint lost: $endpointId")
            discoveredEndpoints.remove(endpointId)
        }
    }

    override fun startServer(deviceName: String, config: TransportConfig) {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(config.strategy).build()

        connectionsClient.startAdvertising(
            deviceName,
            config.serviceId,
            serverConnectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            _serverIsRunning.value = true
            scope.launch {
                _advertisingState.emit(AdvertisingStateInfo(AdvertisingState.ADVERTISING, null))
            }
        }.addOnFailureListener { e: Exception? ->
            Log.e(TAG, "Failed to start server", e)
            _serverIsRunning.value = false
            scope.launch {
                _advertisingState.emit(AdvertisingStateInfo(AdvertisingState.FAILED, e))
            }
        }
    }

    override fun stopServer() {
        connectionsClient.stopAdvertising()
        connectionsClient.stopAllEndpoints()
        _serverIsRunning.value = false
        connectedEndpointIds.clear()
        scope.launch {
            _advertisingState.emit(AdvertisingStateInfo(AdvertisingState.NOT_ADVERTISING, null))
        }
    }

    override fun broadcast(payload: ByteArray) {
        if (connectedEndpointIds.isEmpty()) {
            return
        }
        sendPayload(connectedEndpointIds.toList(), payload)
    }

    override fun startClient(endpointId: String, deviceName: String, config: TransportConfig) {
        connectionsClient.stopDiscovery()
        connectionsClient.requestConnection(
            deviceName, endpointId, clientConnectionLifecycleCallback
        )
    }

    override fun disconnectFromEndpoint(endpointId: String) {
        connectionsClient.disconnectFromEndpoint(endpointId)
    }

    override fun stopAllEndpoints() {
        connectionsClient.stopAllEndpoints()
        connectedEndpointIds.clear()
    }

    override fun send(endpointId: String, payload: ByteArray) {
        sendPayload(listOf(endpointId), payload)
    }

    override fun startDiscovery(config: TransportConfig) {
        if (_discoveryState.value.state == DiscoveryState.DISCOVERING) {
            Log.w(TAG, "Already discovering, ignoring start request")
            return
        }

        discoveredEndpoints.clear()

        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(config.strategy).build()

        connectionsClient.startDiscovery(
            config.serviceId,
            discoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            Log.d(TAG, "Discovery started")
            _discoveryState.value = DiscoveryStateInfo(DiscoveryState.DISCOVERING, null)
        }.addOnFailureListener { e: Exception? ->
            Log.e(TAG, "Failed to start discovery", e)
            _discoveryState.value = DiscoveryStateInfo(DiscoveryState.FAILED, e)
        }
    }

    override fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        _discoveryState.value = DiscoveryStateInfo(DiscoveryState.NOT_DISCOVERING, null)
    }

    private fun sendPayload(endpointIds: List<String>, payload: ByteArray) {
        Log.d(TAG, "Sending payload (${payload.size} bytes)")
        connectionsClient.sendPayload(
            endpointIds, Payload.fromBytes(payload)
        ).addOnSuccessListener {
            Log.d(TAG, "Payload sent")
        }.addOnFailureListener { e: Exception? ->
            Log.e(TAG, "Failed to send payload", e)
        }
    }
}
