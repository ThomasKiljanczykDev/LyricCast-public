/*
 * Created by Tomasz Kiljanczyk on 5/27/26, 9:55 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/27/26, 9:55 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.nearby

enum class AdvertisingState {
    ADVERTISING, NOT_ADVERTISING, FAILED
}

enum class ConnectionState {
    DISCONNECTED, CONNECTED, FAILED
}

data class AdvertisingStateInfo(val state: AdvertisingState, val exception: Exception?)

data class DeviceConnectionInfo(val deviceName: String, val connectionState: ConnectionState)

sealed interface ClientConnectionEvent {
    val endpointId: String

    data class Initiated(
        override val endpointId: String,
        val deviceName: String
    ) : ClientConnectionEvent

    data class Result(
        override val endpointId: String,
        val success: Boolean
    ) : ClientConnectionEvent

    data class Disconnected(override val endpointId: String) : ClientConnectionEvent
}

enum class DiscoveryState {
    DISCOVERING, NOT_DISCOVERING, FAILED
}

data class DiscoveryStateInfo(val state: DiscoveryState, val exception: Exception?)

data class DiscoveredDevice(val endpointId: String, val endpointName: String)
