/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 12:35 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 12:33 AM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.nearby.NearbyPermissions
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SlidePreview
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongInfo
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.currentWindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.isWidthExpanded
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.choose_session.ChooseSessionDialog
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.shared.preview.SessionPreviewData
import kotlinx.coroutines.launch

/** Caps the read-only running order so it never crowds out the slide itself. */
private val SETLIST_LIST_HEIGHT = 200.dp

/** Side-pane width for the running order on expanded-width windows. */
private val SETLIST_COLUMN_WIDTH = 400.dp

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
            val allPermissionsGranted = NearbyPermissions.areAllPermissionsGranted(context)

            permissionsGranted = allPermissionsGranted
            if (allPermissionsGranted) {
                showChooseSessionDialog = true
            } else {
                permissionRequestLauncher.launch(NearbyPermissions.REQUIRED_PERMISSIONS)
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
        setlist = viewModel.state.setlist,
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
                    //noinspection MissingPermission
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
    setlist: SetlistInfo?,
    snackbarHostState: SnackbarHostState,
    onNavigateUp: () -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowSizeClass()
) {
    val showSlideInformation = songTitle.isNotBlank() || totalSlideCount > 0
    val showSetlist = setlist != null && setlist.songs.isNotEmpty()

    @Composable
    fun SongInfoSection() {
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
    }

    @Composable
    fun Slide(slideModifier: Modifier = Modifier) {
        SlidePreview(
            // SlidePreview will handle empty text properly
            slideText = slideText.ifBlank { "" },
            modifier = slideModifier,
            fontSize = 18
        )
    }

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
        if (windowSizeClass.isWidthExpanded()) {
            // Wide window: the running order moves beside the slide instead of under it, so
            // the slide keeps the full height.
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SongInfoSection()

                    Slide(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                AnimatedVisibility(
                    visible = showSetlist,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    setlist?.let {
                        SessionClientSetlistList(
                            setlist = it,
                            modifier = Modifier
                                .width(SETLIST_COLUMN_WIDTH)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SongInfoSection()

                Slide(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                AnimatedVisibility(
                    visible = showSetlist,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    // Capped, not weighted — slide must not shrink as the list grows.
                    setlist?.let {
                        SessionClientSetlistList(
                            setlist = it,
                            modifier = Modifier.height(SETLIST_LIST_HEIGHT)
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSessionClientScreen() {
    LyricCastTheme {
        SessionClientScreen(
            songTitle = SessionPreviewData.sampleSongTitle,
            slideText = SessionPreviewData.sampleSlideText,
            currentSlide = 0, // 0-based index for SongInfo component
            totalSlideCount = SessionPreviewData.sampleSlideCount,
            setlist = null,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateUp = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewSessionClientScreenWithSetlist() {
    LyricCastTheme {
        SessionClientScreen(
            songTitle = SessionPreviewData.sampleSongTitle,
            slideText = SessionPreviewData.sampleSlideText,
            currentSlide = 0,
            totalSlideCount = SessionPreviewData.sampleSlideCount,
            setlist = SessionPreviewData.sampleSetlist,
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
            setlist = null,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateUp = {}
        )
    }
}
