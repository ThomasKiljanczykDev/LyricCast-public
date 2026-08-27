/*
 * Created by Tomasz Kiljanczyk on 5/27/26, 11:30 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/27/26, 11:30 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.nearby

import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Seam over GMS Nearby Connections, shared by the casting session and the sync flows.
 *
 * Host operations advertise this device and broadcast to all connected endpoints.
 * Client operations request a connection to a known endpoint id and exchange payloads
 * with that single endpoint. Discovery operations enumerate advertising devices.
 * Both sides observe [receivedPayload] for incoming bytes.
 *
 * The [TransportConfig] argument selects service id + strategy:
 * [TransportConfig.Session] for the live-session protocol, [TransportConfig.Sync] for
 * import/export.
 */
interface PayloadTransport {
    val serverIsRunning: StateFlow<Boolean>
    val deviceConnectionInfo: Flow<DeviceConnectionInfo>
    val advertisingState: Flow<AdvertisingStateInfo>
    val clientConnectionEvents: Flow<ClientConnectionEvent>
    val receivedPayload: Flow<ReceivedPayload>
    val discoveryState: StateFlow<DiscoveryStateInfo>
    val discoveredDevices: Flow<DiscoveredDevice>

    fun startServer(deviceName: String, config: TransportConfig = TransportConfig.Session)
    fun stopServer()
    fun broadcast(payload: ByteArray)

    fun startClient(
        endpointId: String,
        deviceName: String,
        config: TransportConfig = TransportConfig.Session
    )
    fun disconnectFromEndpoint(endpointId: String)
    fun stopAllEndpoints()

    fun send(endpointId: String, payload: ByteArray)

    fun startDiscovery(config: TransportConfig)
    fun stopDiscovery()
}
