/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:56 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:56 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.shared.menu.gms_nearby_session.dialog

import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.application.LyricCastApplication
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastTextField
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.GmsNearbySessionServerContext

@Composable
fun StartSessionServerDialog(
    onDismiss: () -> Unit,
    onSessionStarted: () -> Unit,
    onShowPermissionDialog: (messageRes: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StartSessionServerDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            onShowPermissionDialog(R.string.dialog_fragment_start_session_missing_permissions)
        }
    }

    // Auto-populate device name on first load
    LaunchedEffect(Unit) {
        if (state.sessionName.isEmpty()) {
            val deviceName = Settings.Global.getString(
                context.contentResolver,
                Settings.Global.DEVICE_NAME
            ) ?: ""
            viewModel.updateSessionName(deviceName)
        }

        // Request permissions
        permissionLauncher.launch(LyricCastApplication.PERMISSIONS)
    }

    // Handle advertising state changes
    val currentOnSessionStarted by rememberUpdatedState(onSessionStarted)
    LaunchedEffect(state.advertisingState) {
        when (state.advertisingState) {
            GmsNearbySessionServerContext.AdvertisingState.ADVERTISING -> {
                currentOnSessionStarted()
            }

            else -> { /* Handle in UI */
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.dialog_fragment_start_session_title))
        },
        text = {
            Column(
                modifier = Modifier.widthIn(min = 300.dp)
            ) {
                LyricCastTextField(
                    value = state.sessionName,
                    onValueChange = viewModel::updateSessionName,
                    label = stringResource(R.string.hint_session_name),
                    errorText = if (!state.isSessionNameValid) state.sessionNameError?.asString() else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error message from server
                state.errorMessageRes?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val started = viewModel.startSessionServer()
                    if (!started) {
                        // Will show validation error in UI
                    }
                },
                enabled = state.isSessionNameValid && !state.isStartingSession
            ) {
                if (state.isStartingSession) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(stringResource(R.string.dialog_fragment_start_session_start))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !state.isStartingSession
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !state.isStartingSession,
            dismissOnClickOutside = !state.isStartingSession
        ),
        modifier = modifier
    )
}

@Suppress("UnusedParameter") // advertisingState is kept for API symmetry with the stateful overload; not yet
// surfaced in this stateless UI.
@Composable
fun StartSessionServerDialogStateless(
    sessionName: String,
    isSessionNameValid: Boolean,
    sessionNameError: String?,
    isStartingSession: Boolean,
    advertisingState: GmsNearbySessionServerContext.AdvertisingState,
    errorMessageRes: Int?,
    onSessionNameChange: (String) -> Unit,
    onStartSession: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.dialog_fragment_start_session_title))
        },
        text = {
            Column(
                modifier = Modifier.widthIn(min = 300.dp)
            ) {
                LyricCastTextField(
                    value = sessionName,
                    onValueChange = onSessionNameChange,
                    label = stringResource(R.string.hint_session_name),
                    errorText = if (!isSessionNameValid) sessionNameError else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error message from server
                errorMessageRes?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onStartSession,
                enabled = isSessionNameValid && !isStartingSession
            ) {
                if (isStartingSession) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                Text(stringResource(R.string.dialog_fragment_start_session_start))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isStartingSession
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isStartingSession,
            dismissOnClickOutside = !isStartingSession
        ),
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun StartSessionServerDialogPreview() {
    LyricCastTheme {
        Surface {
            var sessionName by remember { mutableStateOf("My Device") }
            var isStartingSession by remember { mutableStateOf(false) }

            StartSessionServerDialogStateless(
                sessionName = sessionName,
                isSessionNameValid = sessionName.trim().isNotBlank(),
                sessionNameError = if (sessionName.trim()
                        .isBlank()
                ) "Session name cannot be empty" else null,
                isStartingSession = isStartingSession,
                advertisingState = GmsNearbySessionServerContext.AdvertisingState.NOT_ADVERTISING,
                errorMessageRes = null,
                onSessionNameChange = { sessionName = it },
                onStartSession = { isStartingSession = true },
                onDismiss = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StartSessionServerDialogLoadingPreview() {
    LyricCastTheme {
        Surface {
            StartSessionServerDialogStateless(
                sessionName = "My Device",
                isSessionNameValid = true,
                sessionNameError = null,
                isStartingSession = true,
                advertisingState = GmsNearbySessionServerContext.AdvertisingState.NOT_ADVERTISING,
                errorMessageRes = null,
                onSessionNameChange = {},
                onStartSession = {},
                onDismiss = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StartSessionServerDialogErrorPreview() {
    LyricCastTheme {
        Surface {
            StartSessionServerDialogStateless(
                sessionName = "",
                isSessionNameValid = false,
                sessionNameError = "Session name cannot be empty",
                isStartingSession = false,
                advertisingState = GmsNearbySessionServerContext.AdvertisingState.FAILED,
                errorMessageRes = R.string.dialog_fragment_start_session_session_start_failed,
                onSessionNameChange = {},
                onStartSession = {},
                onDismiss = {}
            )
        }
    }
}
