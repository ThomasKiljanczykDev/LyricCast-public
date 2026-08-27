/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:52 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:50 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.menu.gms_nearby_session.dialog

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.GmsNearbySessionServerContext
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.GmsNearbySessionServerContext.AdvertisingState
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

interface StartSessionServerDialogState {
    val sessionName: String
    val sessionNameError: UiText?
    val isSessionNameValid: Boolean
    val isStartingSession: Boolean
    val advertisingState: AdvertisingState
    val errorMessageRes: Int?
}

class MutableStartSessionServerDialogState : StartSessionServerDialogState {
    override var sessionName by mutableStateOf("")
    override var isStartingSession by mutableStateOf(false)
    override var advertisingState by mutableStateOf(AdvertisingState.NOT_ADVERTISING)
    override var errorMessageRes by mutableStateOf<Int?>(null)

    override val sessionNameError by derivedStateOf {
        if (sessionName.trim().isBlank()) {
            UiText.StringResource(R.string.dialog_fragment_start_session_error_empty_name)
        } else {
            null
        }
    }

    override val isSessionNameValid by derivedStateOf {
        sessionNameError == null
    }
}

@HiltViewModel
class StartSessionServerDialogViewModel @Inject constructor(
    private val gmsNearbySessionServerContext: GmsNearbySessionServerContext
) : ViewModel() {

    private val _state = MutableStartSessionServerDialogState()
    val state: StartSessionServerDialogState get() = _state

    init {
        // Monitor advertising state changes
        gmsNearbySessionServerContext.advertisingState
            .map { advertisingStateInfo ->
                // Handle error messages
                val errorRes = if (advertisingStateInfo.exception is ApiException &&
                    advertisingStateInfo.exception.status.statusCode == 8038
                ) {
                    R.string.dialog_fragment_start_session_session_start_missing_permissions
                } else if (advertisingStateInfo.state == AdvertisingState.FAILED) {
                    R.string.dialog_fragment_start_session_session_start_failed
                } else {
                    null
                }

                _state.advertisingState = advertisingStateInfo.state
                _state.errorMessageRes = errorRes
                _state.isStartingSession =
                    advertisingStateInfo.state == AdvertisingState.NOT_ADVERTISING

                advertisingStateInfo
            }
            .onEach { /* State already updated above */ }
            .launchIn(viewModelScope)
    }

    fun updateSessionName(name: String) {
        _state.sessionName = name
    }

    fun startSessionServer(): Boolean {
        if (!state.isSessionNameValid) {
            return false
        }

        _state.isStartingSession = true
        _state.errorMessageRes = null

        gmsNearbySessionServerContext.startServer(state.sessionName.trim())
        return true
    }

    fun clearError() {
        _state.errorMessageRes = null
    }
}
