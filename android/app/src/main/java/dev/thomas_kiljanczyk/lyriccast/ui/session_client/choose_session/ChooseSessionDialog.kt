/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 7:11 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 6:44 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.session_client.choose_session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ChooseSessionDialog(
    onDismiss: () -> Unit, onSessionSelected: (String) -> Unit
) {
    val dialogKey = remember { Uuid.random().toString() }
    val viewModel: ChooseSessionDialogViewModel = hiltViewModel(key = dialogKey)

    DisposableEffect(Unit) {
        viewModel.reset()
        viewModel.startDiscovery()

        onDispose {
            viewModel.stopDiscovery()
        }
    }

    ChooseSessionDialog(
        state = viewModel.state, onDismiss = onDismiss, onSessionSelected = { item ->
            viewModel.pickDevice(item)
            onSessionSelected(item.endpointId)
        })

    if (viewModel.state.isConnecting) {
        ConnectingDialog()
    }
}

@Composable
fun ChooseSessionDialog(
    state: ChooseSessionDialogState,
    onDismiss: () -> Unit = {},
    onSessionSelected: (GmsNearbySessionItem) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss, title = {
            Text(text = stringResource(R.string.dialog_fragment_choose_session_title))
        }, text = {
            ChooseSessionDialogContent(
                devices = state.devices,
                hasError = state.hasError,
                onSessionSelected = onSessionSelected
            )
        }, confirmButton = {
            // No confirm button needed
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close))
            }
        }, properties = DialogProperties(
            dismissOnBackPress = false, dismissOnClickOutside = false
        )
    )
}

@Composable
private fun ChooseSessionDialogContent(
    devices: ImmutableList<GmsNearbySessionItem>,
    hasError: Boolean,
    onSessionSelected: (GmsNearbySessionItem) -> Unit
) {
    val view = LocalView.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (devices.isEmpty()) {
            // Loading state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (hasError) {
                        stringResource(R.string.session_client_failed_to_start_lookup)
                    } else {
                        stringResource(R.string.dialog_fragment_choose_session_looking_for_session)
                    }, modifier = Modifier.padding(bottom = 8.dp)
                )

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(), color = if (hasError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        } else {
            // Device list
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                LazyColumn(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(devices, key = { device -> device.endpointId }) { device ->
                        SessionItem(
                            device = device, onClick = {
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
                                onSessionSelected(device)
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionItem(
    device: GmsNearbySessionItem, onClick: () -> Unit
) {
    Card(
        onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {

        Text(
            modifier = Modifier.padding(16.dp),
            text = device.deviceName,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ConnectingDialog() {
    AlertDialog(onDismissRequest = { /* Don't allow dismissing while connecting */ }, title = {
        Text(text = stringResource(R.string.session_client_connecting))
    }, text = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = stringResource(R.string.session_client_connecting_please_wait))
        }
    }, confirmButton = {}, dismissButton = {})
}

@PreviewLightDark
@Composable
fun PreviewChooseSessionDialog() {
    LyricCastTheme {
        Surface {
            ChooseSessionDialog(
                state = MutableChooseSessionDialogState().apply {
                    devices = persistentListOf(
                        GmsNearbySessionItem("endpoint1", "John's Tablet"),
                        GmsNearbySessionItem("endpoint2", "Jack's Phone"),
                        GmsNearbySessionItem("endpoint3", "Jane's Tablet")
                    )
                    hasError = false
                    selectedEndpointId = null
                    isConnecting = false
                })
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewChooseSessionDialogLoading() {
    LyricCastTheme {
        Surface {
            ChooseSessionDialog(
                state = MutableChooseSessionDialogState().apply {
                    devices = persistentListOf()
                    hasError = false
                    selectedEndpointId = null
                    isConnecting = false
                })
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewChooseSessionDialogError() {
    LyricCastTheme {
        Surface {
            ChooseSessionDialog(
                state = MutableChooseSessionDialogState().apply {
                    devices = persistentListOf()
                    hasError = true
                    selectedEndpointId = null
                    isConnecting = false
                })
        }
    }
}