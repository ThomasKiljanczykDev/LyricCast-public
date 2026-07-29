/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 12:07 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 12:05 AM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.application.LyricCastApplication
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import dev.thomas_kiljanczyk.lyriccast.domain.models.UiText
import dev.thomas_kiljanczyk.lyriccast.ui.main.import_dialog.ImportDialog
import dev.thomas_kiljanczyk.lyriccast.ui.main.setlists.SetlistsScreen
import dev.thomas_kiljanczyk.lyriccast.ui.main.setlists.SetlistsScreenState
import dev.thomas_kiljanczyk.lyriccast.ui.main.setlists.SetlistsScreenViewModel
import dev.thomas_kiljanczyk.lyriccast.ui.main.songs.SongsScreen
import dev.thomas_kiljanczyk.lyriccast.ui.main.songs.SongsScreenState
import dev.thomas_kiljanczyk.lyriccast.ui.main.songs.SongsScreenViewModel
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.ProgressDialog
import dev.thomas_kiljanczyk.lyriccast.ui.shared.menu.gms_nearby_session.dialog.StartSessionServerDialog
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.util.UUID

enum class MainTab(val titleRes: Int, val icon: ImageVector) {
    SONGS(R.string.title_songs, Icons.Rounded.MusicNote),
    SETLISTS(R.string.title_setlists, Icons.AutoMirrored.Rounded.PlaylistPlay),
    JOIN_SESSION(R.string.main_activity_tab_join_session, Icons.AutoMirrored.Rounded.Login)
}

private const val TAG = "MainScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    activity: ComponentActivity,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToSongEditor: (UUID?) -> Unit = {},
    onNavigateToSetlistEditor: (UUID?, List<UUID>?) -> Unit = { _, _ -> },
    onNavigateToCategoryManager: () -> Unit = {},
    onNavigateToSessionClient: () -> Unit = {},
    onNavigateToSongControls: (UUID) -> Unit = {},
    onNavigateToSetlistControls: (UUID) -> Unit = {},
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val songsViewModel: SongsScreenViewModel = viewModel(viewModelStoreOwner = activity)
    val setlistsViewModel: SetlistsScreenViewModel = viewModel(viewModelStoreOwner = activity)

    MainScreen(
        state = viewModel.state,
        songsState = songsViewModel.state,
        setlistsState = setlistsViewModel.state,
        onTabSelected = viewModel::selectTab,
        onShowProgressDialog = viewModel::showProgressDialog,
        onHideProgressDialog = viewModel::hideProgressDialog,
        onExportAll = viewModel::exportAll,
        onHandleImport = viewModel::handleImport,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToSongEditor = onNavigateToSongEditor,
        onNavigateToSetlistEditor = onNavigateToSetlistEditor,
        onNavigateToCategoryManager = onNavigateToCategoryManager,
        onNavigateToSessionClient = onNavigateToSessionClient,
        onNavigateToSongControls = onNavigateToSongControls,
        onNavigateToSetlistControls = onNavigateToSetlistControls,
        // Songs actions
        onSongsExitSelectionMode = songsViewModel::exitSelectionMode,
        onSongsDeleteSelected = songsViewModel::deleteSelectedSongs,
        onSongsExportSelected = songsViewModel::exportSelectedSongs,
        // Setlists actions
        onSetlistsExitSelectionMode = setlistsViewModel::exitSelectionMode,
        onSetlistsDeleteSelected = setlistsViewModel::deleteSelectedSetlists,
        onSetlistsExportSelected = setlistsViewModel::exportSelectedSetlists
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    state: MainScreenState,
    songsState: SongsScreenState,
    setlistsState: SetlistsScreenState,
    onTabSelected: (MainTab) -> Unit,
    onShowProgressDialog: (UiText) -> Unit,
    onHideProgressDialog: () -> Unit,
    onExportAll: suspend (String, OutputStream) -> Unit,
    onHandleImport: suspend (String, java.io.InputStream, ImportFormat, ImportOptions) -> Boolean,
    onNavigateToSettings: () -> Unit,
    onNavigateToSongEditor: (UUID?) -> Unit = {},
    onNavigateToSetlistEditor: (UUID?, List<UUID>?) -> Unit = { _, _ -> },
    onNavigateToCategoryManager: () -> Unit = {},
    onNavigateToSessionClient: () -> Unit = {},
    onNavigateToSongControls: (UUID) -> Unit = {},
    onNavigateToSetlistControls: (UUID) -> Unit = {},
    // Songs actions
    onSongsExitSelectionMode: () -> Unit,
    onSongsDeleteSelected: suspend () -> Unit,
    onSongsExportSelected: suspend (String, OutputStream) -> Unit,
    // Setlists actions
    onSetlistsExitSelectionMode: () -> Unit,
    onSetlistsDeleteSelected: suspend () -> Unit,
    onSetlistsExportSelected: suspend (String, OutputStream) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showImportDialog by remember { mutableStateOf(false) }
    var showStartSessionDialog by remember { mutableStateOf(false) }
    var showPermissionsRejectedDialog by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    // Import state to store between dialog and file picker
    var pendingImportFormat by remember { mutableStateOf<ImportFormat?>(null) }
    var pendingImportOptions by remember { mutableStateOf<ImportOptions?>(null) }

    // Permission checking state
    var pendingStartSessionDialog by remember { mutableStateOf(false) }

    // Reset selection modes when switching tabs
    LaunchedEffect(state.selectedTab) {
        when (state.selectedTab) {
            MainTab.SONGS -> {
                // Exit setlists selection mode when switching to songs
                if (setlistsState.isInSelectionMode) {
                    onSetlistsExitSelectionMode()
                }
            }

            MainTab.SETLISTS -> {
                // Exit songs selection mode when switching to setlists
                if (songsState.isInSelectionMode) {
                    onSongsExitSelectionMode()
                }
            }

            MainTab.JOIN_SESSION -> {
                // Exit both selection modes when switching to join session
                if (songsState.isInSelectionMode) {
                    onSongsExitSelectionMode()
                }
                if (setlistsState.isInSelectionMode) {
                    onSetlistsExitSelectionMode()
                }
            }
        }
    }

    // Permission checking
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (activity == null) {
            return@rememberLauncherForActivityResult
        }

        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            // If user was trying to start a session, show the dialog now
            if (pendingStartSessionDialog) {
                showStartSessionDialog = true
                pendingStartSessionDialog = false
            }
        } else {
            // Reset pending state if permissions were denied
            pendingStartSessionDialog = false
            // Show permissions rejected dialog
            showPermissionsRejectedDialog = true
        }
    }


    // Activity launchers
    val exportAllLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { selectedUri ->
            coroutineScope.launch(Dispatchers.IO) {
                onShowProgressDialog(UiText.StringResource(R.string.main_activity_export_preparing_data))
                try {
                    val outputStream = context.contentResolver.openOutputStream(selectedUri)!!
                    onExportAll(context.cacheDir.canonicalPath, outputStream)
                    outputStream.close()
                    onHideProgressDialog()
                } catch (e: Exception) {
                    Log.e(TAG, "Error exporting", e)
                    onHideProgressDialog()
                }
            }
        }
    }

    val exportSongsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { selectedUri ->
            coroutineScope.launch(Dispatchers.IO) {
                onShowProgressDialog(UiText.StringResource(R.string.main_activity_export_preparing_data))
                try {
                    val outputStream = context.contentResolver.openOutputStream(selectedUri)!!
                    onSongsExportSelected(context.cacheDir.canonicalPath, outputStream)
                    outputStream.close()
                    onHideProgressDialog()
                } catch (e: Exception) {
                    Log.e(TAG, "Error exporting", e)
                    onHideProgressDialog()
                }
            }
        }
    }

    val exportSetlistsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { selectedUri ->
            coroutineScope.launch(Dispatchers.IO) {
                onShowProgressDialog(UiText.StringResource(R.string.main_activity_export_preparing_data))
                try {
                    val outputStream = context.contentResolver.openOutputStream(selectedUri)!!
                    onSetlistsExportSelected(context.cacheDir.canonicalPath, outputStream)
                    outputStream.close()
                    onHideProgressDialog()
                } catch (e: Exception) {
                    Log.e(TAG, "Error exporting", e)
                    onHideProgressDialog()
                }
            }
        }
    }

    val importSuccessfulString = stringResource(R.string.main_activity_import_successful)
    val importFailedString = stringResource(R.string.main_activity_import_failed)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            val format = pendingImportFormat
            val options = pendingImportOptions

            if (format != null && options != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(selectedUri)!!
                        val success = onHandleImport(
                            context.cacheDir.canonicalPath,
                            inputStream,
                            format,
                            options
                        )
                        inputStream.close()

                        launch(Dispatchers.Main) {
                            if (success) {
                                snackbarHostState.showSnackbar(
                                    importSuccessfulString
                                )
                            } else {
                                snackbarHostState.showSnackbar(
                                    importFailedString
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error importing", e)
                        onHideProgressDialog()
                        launch(Dispatchers.Main) {
                            snackbarHostState.showSnackbar(
                                importFailedString
                            )
                        }
                    } finally {
                        // Clear pending import state
                        pendingImportFormat = null
                        pendingImportOptions = null
                    }
                }
            }
        }
    }

    val blurRadius by animateDpAsState(
        targetValue = if (fabExpanded) 6.dp else 0.dp,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
        label = "blur_radius"
    )

    Box {
        // Blurred background content
        Box(Modifier.blur(blurRadius)) {
            Scaffold(
                topBar = {
                    AnimatedContent(
                        targetState = when {
                            songsState.isInSelectionMode && state.selectedTab == MainTab.SONGS -> "songs_selection"
                            setlistsState.isInSelectionMode && state.selectedTab == MainTab.SETLISTS -> "setlists_selection"
                            else -> "normal"
                        },
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "top_bar_animation"
                    ) { topBarState ->
                        when (topBarState) {
                            "songs_selection" -> SongsActionModeTopBar(
                                selectedCount = songsState.selectedSongs.size,
                                onExitSelectionMode = onSongsExitSelectionMode,
                                onEditSong = {
                                    val selectedSong = songsState.selectedSongs.firstOrNull()
                                    selectedSong?.let {
                                        onNavigateToSongEditor(it.id)
                                        onSongsExitSelectionMode()
                                    }
                                },
                                onAddToSetlist = {
                                    val selectedSongIds =
                                        songsState.selectedSongs.map { it.id }.toTypedArray()
                                    onNavigateToSetlistEditor(null, selectedSongIds.toList())
                                    onSongsExitSelectionMode()
                                },
                                onExportSongs = {
                                    exportSongsLauncher.launch("")
                                    onSongsExitSelectionMode()
                                },
                                onDeleteSongs = {
                                    coroutineScope.launch {
                                        onSongsDeleteSelected()
                                    }
                                }
                            )

                            "setlists_selection" -> SetlistsActionModeTopBar(
                                selectedCount = setlistsState.selectedSetlists.size,
                                onExitSelectionMode = onSetlistsExitSelectionMode,
                                onEditSetlist = {
                                    val selectedSetlist = setlistsState.selectedSetlists.first()
                                    onNavigateToSetlistEditor(
                                        selectedSetlist.id,
                                        null
                                    )
                                    onSetlistsExitSelectionMode()
                                },
                                onExportSetlists = {
                                    exportSetlistsLauncher.launch("")
                                    onSetlistsExitSelectionMode()
                                },
                                onDeleteSetlists = {
                                    coroutineScope.launch {
                                        onSetlistsDeleteSelected()
                                    }
                                }
                            )

                            else -> MainScreenTopBar(
                                onNavigateToCategoryManager = onNavigateToCategoryManager,
                                onImport = {
                                    showImportDialog = true
                                },
                                onExport = {
                                    exportAllLauncher.launch("")
                                },
                                onNavigateToSettings = onNavigateToSettings,
                                onStartSession = {
                                    if (activity == null) return@MainScreenTopBar

                                    // Check if all required permissions are granted
                                    val allPermissionsGranted =
                                        LyricCastApplication.PERMISSIONS.all { permission ->
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                permission
                                            ) == PackageManager.PERMISSION_GRANTED
                                        }

                                    if (allPermissionsGranted) {
                                        showStartSessionDialog = true
                                    } else {
                                        // Request permissions first, mark that we want to show start session dialog after
                                        pendingStartSessionDialog = true
                                        permissionRequestLauncher.launch(LyricCastApplication.PERMISSIONS)
                                    }
                                }
                            )
                        }
                    }
                },
                bottomBar = {
                    NavigationBar {
                        val cannotJoinSessionString =
                            stringResource(R.string.main_activity_cannot_join_session)
                        MainTab.entries.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = state.selectedTab == tab,
                                onClick = {
                                    if (tab != MainTab.JOIN_SESSION) {
                                        onTabSelected(tab)
                                        return@NavigationBarItem
                                    }

                                    if (state.isSessionServerRunning) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                cannotJoinSessionString
                                            )
                                        }
                                        return@NavigationBarItem
                                    }

                                    onNavigateToSessionClient()
                                },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                label = { Text(stringResource(tab.titleRes)) }
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                // Progress Dialog
                if (state.showProgressDialog) {
                    ProgressDialog(
                        message = state.progressMessage
                            ?: UiText.StringResource(R.string.progress_processing),
                        showOkButton = state.progressCompleted,
                        onDismiss = onHideProgressDialog
                    )
                }

                // Import Dialog
                if (showImportDialog) {
                    ImportDialog(
                        onDismiss = { showImportDialog = false },
                        onImport = { importDialogState ->
                            showImportDialog = false
                            // Store import options for when file is selected
                            pendingImportFormat = importDialogState.importFormat
                            pendingImportOptions = ImportOptions(
                                deleteAll = importDialogState.deleteAll,
                                replaceOnConflict = importDialogState.replaceOnConflict
                            )
                            // Launch file picker with the right MIME type based on format
                            val mimeType = when (importDialogState.importFormat) {
                                ImportFormat.LYRIC_CAST -> "application/zip"
                                ImportFormat.OPEN_SONG -> "application/zip"
                                else -> "*/*"
                            }
                            importLauncher.launch(mimeType)
                        }
                    )
                }

                // Start Session Dialog
                if (showStartSessionDialog) {
                    StartSessionServerDialog(
                        onDismiss = { showStartSessionDialog = false },
                        onSessionStarted = {
                            showStartSessionDialog = false
                        },
                        onShowPermissionDialog = { messageRes ->
                            showStartSessionDialog = false
                        }
                    )
                }

                // Permissions Rejected Dialog
                if (showPermissionsRejectedDialog) {
                    AlertDialog(
                        onDismissRequest = { showPermissionsRejectedDialog = false },
                        text = {
                            Text(stringResource(R.string.dialog_fragment_start_session_missing_permissions))
                        },
                        dismissButton = {
                            TextButton(onClick = { showPermissionsRejectedDialog = false }) {
                                Text(stringResource(R.string.ignore))
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showPermissionsRejectedDialog = false
                                    // Open app settings
                                    val packageName = context.packageName
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    context.startActivity(intent)
                                }
                            ) {
                                Text(stringResource(R.string.launch_activity_go_to_settings))
                            }
                        }
                    )
                }

                AnimatedContent(
                    targetState = state.selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) { selectedTab ->
                    when (selectedTab) {
                        MainTab.SONGS -> SongsScreen(
                            onNavigateToSongControls = onNavigateToSongControls
                        )

                        MainTab.SETLISTS -> SetlistsScreen(
                            onNavigateToSetlistControls = onNavigateToSetlistControls
                        )

                        MainTab.JOIN_SESSION -> {
                            // This tab doesn't show content, just launches activity
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("Join Session")
                            }
                        }
                    }
                }
            }
        }

        // Dim overlay
        AnimatedVisibility(
            visible = fabExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { fabExpanded = false }
            )
        }

        // FAB menu above the blur
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, bottom = 120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            MainScreenFloatingActionMenu(
                expanded = fabExpanded,
                onExpandedChange = { fabExpanded = it },
                onAddSong = {
                    onNavigateToSongEditor(null)
                },
                onAddSetlist = {
                    onNavigateToSetlistEditor(null, null)
                }
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun MainScreenPreview() {
    LyricCastTheme {
        // Note: Preview cannot show permission functionality since it requires a real ComponentActivity
        MainScreen(
            state = MutableMainScreenState(),
            songsState = dev.thomas_kiljanczyk.lyriccast.ui.main.songs.MutableSongsScreenState(),
            setlistsState = dev.thomas_kiljanczyk.lyriccast.ui.main.setlists.MutableSetlistsScreenState(),
            onTabSelected = { },
            onShowProgressDialog = {},
            onHideProgressDialog = {},
            onExportAll = { _, _ -> },
            onHandleImport = { _, _, _, _ -> false },
            onNavigateToSettings = {},
            onSongsExitSelectionMode = {},
            onSongsDeleteSelected = {},
            onSongsExportSelected = { _, _ -> },
            onSetlistsExitSelectionMode = {},
            onSetlistsDeleteSelected = {},
            onSetlistsExportSelected = { _, _ -> }
        )
    }
}

