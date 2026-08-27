/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 12:35 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 12:33 AM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.session_client

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.application.LyricCastApplication
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SlidePreview
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongInfo
import dev.thomas_kiljanczyk.lyriccast.ui.session_client.choose_session.ChooseSessionDialog
import kotlinx.coroutines.launch

@Composable
fun SessionClientScreen(
    onNavigateUp: () -> Unit,
    viewModel: SessionClientViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val connectedMessage = stringResource(R.string.session_client_connected)
    val disconnectedMessage = stringResource(R.string.session_client_disconnected)
    val failedToConnectMessage = stringResource(R.string.session_client_failed_to_connect)

    var showChooseSessionDialog by remember { mutableStateOf(false) }
    var showPermissionsRejectedDialog by remember { mutableStateOf(false) }
    var permissionsGranted by remember { mutableStateOf(false) }

    // Permission checking
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.values.all { it }
        permissionsGranted = allPermissionsGranted
        if (allPermissionsGranted) {
            showChooseSessionDialog = true
        } else {
            showPermissionsRejectedDialog = true
        }
    }

    // Check permissions on startup
    LaunchedEffect(Unit) {
        if (activity != null) {
            val allPermissionsGranted = LyricCastApplication.PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }

            permissionsGranted = allPermissionsGranted
            if (allPermissionsGranted) {
                showChooseSessionDialog = true
            } else {
                permissionRequestLauncher.launch(LyricCastApplication.PERMISSIONS)
            }
        }

        // Request latest slide on startup
        viewModel.requestLatestSlide()
    }

    // Handle connection state changes
    LaunchedEffect(viewModel.state.connectionState) {
        when (viewModel.state.connectionState) {
            ConnectionState.CONNECTED -> {
                scope.launch {
                    snackbarHostState.showSnackbar(connectedMessage)
                }
                showChooseSessionDialog = false
            }

            ConnectionState.DISCONNECTED -> {
                scope.launch {
                    snackbarHostState.showSnackbar(disconnectedMessage)
                }
                // Only show choose session dialog if permissions are granted
                if (permissionsGranted) {
                    showChooseSessionDialog = true
                }
            }

            ConnectionState.FAILED -> {
                scope.launch {
                    snackbarHostState.showSnackbar(failedToConnectMessage)
                }
            }

            ConnectionState.UNKNOWN -> {}
        }
    }

    // Clean up when the composable is removed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopClient()
        }
    }

    SessionClientScreen(
        songTitle = viewModel.state.currentSlide.songTitle,
        slideText = viewModel.state.currentSlide.slideText,
        currentSlide = viewModel.state.currentSlide.slideNumber,
        totalSlideCount = viewModel.state.currentSlide.totalSlides,
        snackbarHostState = snackbarHostState,
        onNavigateUp = onNavigateUp
    )

    if (showChooseSessionDialog) {
        ChooseSessionDialog(
            onDismiss = {
                showChooseSessionDialog = false
                onNavigateUp()
            },
            onSessionSelected = { endpointId ->
                try {
                    viewModel.startClient(endpointId)
                } catch (ex: SecurityException) {
                    Log.e("SessionClientScreen", "Failed to start client", ex)
                    showChooseSessionDialog = false
                    onNavigateUp()
                }
            }
        )
    }

    // Permissions Rejected Dialog
    if (showPermissionsRejectedDialog) {
        AlertDialog(
            onDismissRequest = {
                showPermissionsRejectedDialog = false
                onNavigateUp()
            },
            text = {
                Text(stringResource(R.string.dialog_fragment_start_session_missing_permissions))
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionsRejectedDialog = false
                    }
                ) {
                    Text(stringResource(R.string.ignore))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionsRejectedDialog = false
                        // Open app settings
                        val packageName = context.packageName
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                        onNavigateUp()
                    }
                ) {
                    Text(stringResource(R.string.launch_activity_go_to_settings))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionClientScreen(
    songTitle: String,
    slideText: String,
    currentSlide: Int,
    totalSlideCount: Int,
    snackbarHostState: SnackbarHostState,
    onNavigateUp: () -> Unit
) {
    val showSlideInformation = songTitle.isNotBlank() || totalSlideCount > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_session_client)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = showSlideInformation,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SongInfo(
                    songTitle = songTitle,
                    currentSlide = currentSlide,
                    totalSlideCount = totalSlideCount,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            SlidePreview(
                slideText = slideText.ifBlank { "" }, // SlidePreview will handle empty text properly
                modifier = Modifier.fillMaxSize(),
                fontSize = 18
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSessionClientScreen() {
    LyricCastTheme {
        SessionClientScreen(
            songTitle = "Amazing Grace",
            slideText = "Amazing grace, how sweet the sound\nThat saved a wretch like me\n" +
                "I once was lost, but now am found\nWas blind, but now I see",
            currentSlide = 0, // 0-based index for SongInfo component
            totalSlideCount = 4,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateUp = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewSessionClientScreenEmpty() {
    LyricCastTheme {
        SessionClientScreen(
            songTitle = "",
            slideText = "",
            currentSlide = 0,
            totalSlideCount = 0,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateUp = {}
        )
    }
}
