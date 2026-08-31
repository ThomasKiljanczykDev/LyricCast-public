
package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.choose_session

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.nearby.DiscoveryState
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.TransportConfig
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.model.GmsNearbySessionItem
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
    data class DeviceFound(val endpointId: String, val deviceName: String) :
        ChooseSessionDialogEvent

    data class DevicePicked(val device: GmsNearbySessionItem) : ChooseSessionDialogEvent
    data object DiscoveryError : ChooseSessionDialogEvent
}

@HiltViewModel
class ChooseSessionDialogViewModel @Inject constructor(
    private val payloadTransport: PayloadTransport
) : ViewModel() {
    companion object {
        const val TAG: String = "ChooseSessionDialogModel"
    }

    val state: ChooseSessionDialogState
        field = MutableChooseSessionDialogState()

    private val deviceMap = mutableMapOf<String, GmsNearbySessionItem>()
    private var discoveryJob: Job? = null

    fun onEvent(event: ChooseSessionDialogEvent) {
        when (event) {
            is ChooseSessionDialogEvent.Reset -> {
                deviceMap.clear()
                state.apply {
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
                payloadTransport.stopDiscovery()
                discoveryJob?.cancel()
                discoveryJob = null
            }

            is ChooseSessionDialogEvent.DeviceFound -> {
                val deviceItem = GmsNearbySessionItem(event.deviceName, event.endpointId)
                deviceMap[event.endpointId] = deviceItem
                state.devices = deviceMap.values.toImmutableList()
            }

            is ChooseSessionDialogEvent.DevicePicked -> {
                state.apply {
                    selectedEndpointId = event.device.endpointId
                    isConnecting = true
                }
                Log.d(TAG, "Picked: ${event.device.deviceName}")
            }

            is ChooseSessionDialogEvent.DiscoveryError -> {
                Log.e(TAG, "Failed to start discovering")
                state.hasError = true
            }
        }
    }

    private fun startDiscoveryInternal() {
        discoveryJob = payloadTransport.discoveryState
            .onEach { info ->
                if (info.state == DiscoveryState.FAILED) {
                    onEvent(ChooseSessionDialogEvent.DiscoveryError)
                }
            }
            .launchIn(viewModelScope)

        payloadTransport.discoveredDevices
            .onEach { device ->
                onEvent(ChooseSessionDialogEvent.DeviceFound(device.endpointId, device.endpointName))
            }
            .launchIn(viewModelScope)

        payloadTransport.startDiscovery(TransportConfig.Session)
    }

    fun reset() = onEvent(ChooseSessionDialogEvent.Reset)
    fun startDiscovery() = onEvent(ChooseSessionDialogEvent.StartDiscovery)
    fun stopDiscovery() = onEvent(ChooseSessionDialogEvent.StopDiscovery)
    fun pickDevice(item: GmsNearbySessionItem) =
        onEvent(ChooseSessionDialogEvent.DevicePicked(item))
}
