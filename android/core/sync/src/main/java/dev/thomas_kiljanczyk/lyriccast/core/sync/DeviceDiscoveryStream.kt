/*
 * Created by Tomasz Kiljanczyk on 5/27/26, 11:45 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/27/26, 11:45 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.sync

import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * Aggregates the per-event [PayloadTransport.discoveredDevices] flow into a deduped
 * list of [DiscoveredSyncDevice]. [SyncSessionManager] projects this into its session state.
 */
@Singleton
class DeviceDiscoveryStream @Inject constructor(
    private val transport: PayloadTransport
) {
    private val _devices = MutableStateFlow<List<DiscoveredSyncDevice>>(emptyList())
    val devices: StateFlow<List<DiscoveredSyncDevice>> = _devices.asStateFlow()

    fun subscribe(scope: CoroutineScope) {
        transport.discoveredDevices
            .onEach { device ->
                _devices.update { current ->
                    if (current.none { it.endpointId == device.endpointId }) {
                        current + DiscoveredSyncDevice(device.endpointId, device.endpointName)
                    } else {
                        current
                    }
                }
            }
            .launchIn(scope)
    }

    fun clear() {
        _devices.value = emptyList()
    }
}
