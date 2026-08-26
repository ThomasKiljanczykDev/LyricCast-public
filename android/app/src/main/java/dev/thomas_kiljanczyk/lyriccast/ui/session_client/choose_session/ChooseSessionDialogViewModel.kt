/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 7:11 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 6:41 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.session_client.choose_session

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.GmsNearbyConstants
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

interface ChooseSessionDialogState {
    val devices: ImmutableList<GmsNearbySessionItem>
    val hasError: Boolean
    val selectedEndpointId: String?
    val isConnecting: Boolean
}

class MutableChooseSessionDialogState : ChooseSessionDialogState {
    override var devices by mutableStateOf<ImmutableList<GmsNearbySessionItem>>(persistentListOf())
    override var hasError by mutableStateOf(false)
    override var selectedEndpointId by mutableStateOf<String?>(null)
    override var isConnecting by mutableStateOf(false)
}

sealed interface ChooseSessionDialogEvent {
    data object Reset : ChooseSessionDialogEvent
    data object StartDiscovery : ChooseSessionDialogEvent
    data object StopDiscovery : ChooseSessionDialogEvent
    data class DeviceFound(val endpointId: String, val info: DiscoveredEndpointInfo) :
        ChooseSessionDialogEvent

    data class DeviceLost(val endpointId: String) : ChooseSessionDialogEvent
    data class DevicePicked(val device: GmsNearbySessionItem) : ChooseSessionDialogEvent
    data class DiscoveryError(val error: Exception) : ChooseSessionDialogEvent
}

@HiltViewModel
class ChooseSessionDialogViewModel @Inject constructor(
    private val connectionsClient: ConnectionsClient
) : ViewModel() {
    companion object {
        const val TAG: String = "ChooseSessionDialogModel"
    }

    private val _state = MutableChooseSessionDialogState()
    val state: ChooseSessionDialogState get() = _state

    private val deviceMap = mutableMapOf<String, GmsNearbySessionItem>()
    private var endpointDiscoveryCallback: EndpointDiscoveryCallback? = null

    fun onEvent(event: ChooseSessionDialogEvent) {
        when (event) {
            is ChooseSessionDialogEvent.Reset -> {
                deviceMap.clear()
                _state.apply {
                    devices = persistentListOf()
                    hasError = false
                    selectedEndpointId = null
                    isConnecting = false
                }
            }

            is ChooseSessionDialogEvent.StartDiscovery -> {
                startDiscoveryInternal()
            }

            is ChooseSessionDialogEvent.StopDiscovery -> {
                connectionsClient.stopDiscovery()
                endpointDiscoveryCallback = null
            }

            is ChooseSessionDialogEvent.DeviceFound -> {
                val deviceItem = GmsNearbySessionItem(event.info.endpointName, event.endpointId)
                deviceMap[event.endpointId] = deviceItem
                _state.devices = deviceMap.values.toImmutableList()
            }

            is ChooseSessionDialogEvent.DeviceLost -> {
                deviceMap.remove(event.endpointId)
                _state.devices = deviceMap.values.toImmutableList()
            }

            is ChooseSessionDialogEvent.DevicePicked -> {
                _state.apply {
                    selectedEndpointId = event.device.endpointId
                    isConnecting = true
                }
                Log.d(TAG, "Picked: ${event.device.deviceName}")
            }

            is ChooseSessionDialogEvent.DiscoveryError -> {
                Log.e(TAG, "Failed to start discovering", event.error)
                _state.hasError = true
            }
        }
    }

    private fun startDiscoveryInternal() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                onEvent(ChooseSessionDialogEvent.DeviceFound(endpointId, info))
            }

            override fun onEndpointLost(endpointId: String) {
                onEvent(ChooseSessionDialogEvent.DeviceLost(endpointId))
            }
        }

        connectionsClient.startDiscovery(
            GmsNearbyConstants.SERVICE_UUID.toString(),
            endpointDiscoveryCallback!!,
            discoveryOptions
        ).addOnFailureListener { e ->
            onEvent(ChooseSessionDialogEvent.DiscoveryError(e))
        }
    }

    // Backward compatibility functions
    fun reset() = onEvent(ChooseSessionDialogEvent.Reset)
    fun startDiscovery() = onEvent(ChooseSessionDialogEvent.StartDiscovery)
    fun stopDiscovery() = onEvent(ChooseSessionDialogEvent.StopDiscovery)
    fun pickDevice(item: GmsNearbySessionItem) =
        onEvent(ChooseSessionDialogEvent.DevicePicked(item))
}
