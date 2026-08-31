
package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.nearby.NearbyPermissions
import dev.thomas_kiljanczyk.lyriccast.core.sync.ImportInput
import dev.thomas_kiljanczyk.lyriccast.core.sync.PendingImport
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.tourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.ProgressDialog
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.currentWindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.isWidthExpanded
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.import_dialog.ImportDialog
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.SetlistsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.SetlistsScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.SetlistsScreenViewModel
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.SongsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.SongsScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.SongsScreenViewModel
import java.util.UUID
import kotlinx.coroutines.launch

enum class MainTab(val titleRes: Int, val icon: ImageVector) {
    SONGS(R.string.title_songs, Icons.Rounded.MusicNote),
    SETLISTS(R.string.title_setlists, Icons.AutoMirrored.Rounded.PlaylistPlay),
    JOIN_SESSION(R.string.main_activity_tab_join_session, Icons.AutoMirrored.Rounded.Login)
}

private const val TAG = "MainScreen"

/** Clears the bottom navigation bar so the overlaid FAB is not hidden behind it. */
private val NAV_BAR_FAB_CLEARANCE = 80.dp

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
    onStartSessionServer: () -> Unit = {},
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val songsViewModel: SongsScreenViewModel = viewModel(viewModelStoreOwner = activity)
    val setlistsViewModel: SetlistsScreenViewModel = viewModel(viewModelStoreOwner = activity)
    val pendingImport by viewModel.pendingImport.collectAsState()

    MainScreen(
        state = viewModel.state,
        songsState = songsViewModel.state,
        setlistsState = setlistsViewModel.state,
        pendingImport = pendingImport,
        onTabSelected = viewModel::selectTab,
        onShowProgressDialog = viewModel::showProgressDialog,
        onHideProgressDialog = viewModel::hideProgressDialog,
        onExportAll = viewModel::exportAll,
        onHandleImport = viewModel::handleImport,
        onClearPendingImport = viewModel::clearPendingImport,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToSongEditor = onNavigateToSongEditor,
        onNavigateToSetlistEditor = onNavigateToSetlistEditor,
        onNavigateToCategoryManager = onNavigateToCategoryManager,
        onNavigateToSessionClient = onNavigateToSessionClient,
        onNavigateToSongControls = onNavigateToSongControls,
        onNavigateToSetlistControls = onNavigateToSetlistControls,
        onStartSessionServer = onStartSessionServer,
        onSongsExitSelectionMode = songsViewModel::exitSelectionMode,
        onSongsDeleteSelected = songsViewModel::deleteSelectedSongs,
        onSongsExportSelected = songsViewModel::exportSelectedSongs,
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
    onExportAll: suspend (Uri) -> Unit,
    onHandleImport: suspend (ImportInput, ImportFormat, ImportOptions) -> Boolean,
    onNavigateToSettings: () -> Unit,
    pendingImport: PendingImport? = null,
    onClearPendingImport: () -> Unit = {},
    onNavigateToSongEditor: (UUID?) -> Unit = {},
    onNavigateToSetlistEditor: (UUID?, List<UUID>?) -> Unit = { _, _ -> },
    onNavigateToCategoryManager: () -> Unit = {},
    onNavigateToSessionClient: () -> Unit = {},
    onNavigateToSongControls: (UUID) -> Unit = {},
    onNavigateToSetlistControls: (UUID) -> Unit = {},
    // This module never invokes another feature module's screens/navigation directly: the caller
    // (the app-level NavHost) decides what "start a session as server" actually navigates to.
    onStartSessionServer: () -> Unit = {},
    onSongsExitSelectionMode: () -> Unit,
    onSongsDeleteSelected: suspend () -> Unit,
    onSongsExportSelected: suspend (Uri) -> Unit,
    onSetlistsExitSelectionMode: () -> Unit,
    onSetlistsDeleteSelected: suspend () -> Unit,
    onSetlistsExportSelected: suspend (Uri) -> Unit,
    windowSizeClass: WindowSizeClass = currentWindowSizeClass(),
    // The tab bodies default to the Hilt-backed screens, which is what the app wants and what a
    // render outside an Activity cannot build. `:tools:readme-screenshots` passes state-driven
    // ones instead, so the README shots are of this screen rather than a rebuilt lookalike.
    songsContent: @Composable () -> Unit = {
        SongsScreen(onNavigateToSongControls = onNavigateToSongControls)
    },
    setlistsContent: @Composable () -> Unit = {
        SetlistsScreen(onNavigateToSetlistControls = onNavigateToSetlistControls)
    }
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showImportDialog by remember { mutableStateOf(false) }
    var showPermissionsRejectedDialog by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    var pendingImportFormat by remember { mutableStateOf<ImportFormat?>(null) }
    var pendingImportOptions by remember { mutableStateOf<ImportOptions?>(null) }

    var pendingStartSessionDialog by remember { mutableStateOf(false) }

    // A file handed in from outside (ACTION_VIEW/ACTION_SEND) is shown pre-attached, so the user
    // does not need to pick it again; the format is still always picked explicitly below.
    LaunchedEffect(pendingImport) {
        if (pendingImport != null) {
            showImportDialog = true
        }
    }

    val currentOnSongsExitSelectionMode by rememberUpdatedState(onSongsExitSelectionMode)
    val currentOnSetlistsExitSelectionMode by rememberUpdatedState(onSetlistsExitSelectionMode)
    LaunchedEffect(state.selectedTab) {
        when (state.selectedTab) {
            MainTab.SONGS -> {
                if (setlistsState.isInSelectionMode) {
                    currentOnSetlistsExitSelectionMode()
                }
            }

            MainTab.SETLISTS -> {
                if (songsState.isInSelectionMode) {
                    currentOnSongsExitSelectionMode()
                }
            }

            MainTab.JOIN_SESSION -> {
                if (songsState.isInSelectionMode) {
                    currentOnSongsExitSelectionMode()
                }
                if (setlistsState.isInSelectionMode) {
                    currentOnSetlistsExitSelectionMode()
                }
            }
        }
    }

    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (activity == null) {
            return@rememberLauncherForActivityResult
        }

        val allPermissionsGranted = permissions.values.all { it }
        if (allPermissionsGranted) {
            if (pendingStartSessionDialog) {
                onStartSessionServer()
                pendingStartSessionDialog = false
            }
        } else {
            pendingStartSessionDialog = false
            showPermissionsRejectedDialog = true
        }
    }

    val exportAllLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { selectedUri ->
            coroutineScope.launch {
                onShowProgressDialog(UiText.StringResource(R.string.main_activity_export_preparing_data))
                try {
                    onExportAll(selectedUri)
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
            coroutineScope.launch {
                onShowProgressDialog(UiText.StringResource(R.string.main_activity_export_preparing_data))
                try {
                    onSongsExportSelected(selectedUri)
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
            coroutineScope.launch {
                onShowProgressDialog(UiText.StringResource(R.string.main_activity_export_preparing_data))
                try {
                    onSetlistsExportSelected(selectedUri)
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

    // Shared by both import paths: a file freshly picked through the system file picker, and a
    // file that arrived from outside and was pre-attached to the dialog.
    suspend fun performImport(input: ImportInput, format: ImportFormat, options: ImportOptions) {
        try {
            val success = onHandleImport(input, format, options)
            snackbarHostState.showSnackbar(
                if (success) importSuccessfulString else importFailedString
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error importing", e)
            onHideProgressDialog()
            snackbarHostState.showSnackbar(importFailedString)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            val format = pendingImportFormat
            val options = pendingImportOptions

            if (format != null && options != null) {
                coroutineScope.launch {
                    try {
                        performImport(ImportInput.FromUri(selectedUri), format, options)
                    } finally {
                        pendingImportFormat = null
                        pendingImportOptions = null
                    }
                }
            }
        }
    }

    BackHandler(enabled = fabExpanded) {
        fabExpanded = false
    }

    // `navigationSuiteItems` is a plain builder scope, not a composable one, so anything
    // needing composition has to be resolved out here.
    val cannotJoinSessionString = stringResource(R.string.main_activity_cannot_join_session)

    // Drives the FAB's placement too, so the two stay in sync.
    val navLayoutType = if (windowSizeClass.isWidthExpanded()) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteType.NavigationBar
    }

    val systemNavBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val fabBottomPadding = when (navLayoutType) {
        // The rail sits on the start edge and never overlaps a bottom-end FAB.
        NavigationSuiteType.NavigationRail,
        NavigationSuiteType.NavigationDrawer -> systemNavBarHeight

        else -> systemNavBarHeight + NAV_BAR_FAB_CLEARANCE
    }

    Box {
        NavigationSuiteScaffold(
            layoutType = navLayoutType,
            navigationSuiteItems = {
                MainTab.entries.forEach { tab ->
                    item(
                        selected = state.selectedTab == tab,
                        onClick = {
                            if (tab != MainTab.JOIN_SESSION) {
                                onTabSelected(tab)
                                return@item
                            }

                            if (state.isSessionServerRunning) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(cannotJoinSessionString)
                                }
                                return@item
                            }

                            onNavigateToSessionClient()
                        },
                        // The rail and the bottom bar publish the same ids, so a window size
                        // change mid-tour just republishes bounds. Setlists carries no anchor
                        // because Songs stands in for the navigation group as a whole, and two
                        // elements must never publish the same id.
                        modifier = when (tab) {
                            MainTab.SONGS -> Modifier.tourAnchor(TourAnchor.MAIN_TABS)

                            MainTab.JOIN_SESSION ->
                                Modifier.tourAnchor(TourAnchor.MAIN_JOIN_SESSION_TAB)

                            MainTab.SETLISTS -> Modifier
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.titleRes)) }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    AnimatedContent(
                        targetState = when {
                            songsState.isInSelectionMode && state.selectedTab == MainTab.SONGS ->
                                "songs_selection"

                            setlistsState.isInSelectionMode && state.selectedTab == MainTab.SETLISTS ->
                                "setlists_selection"

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

                                    val allPermissionsGranted =
                                        NearbyPermissions.areAllPermissionsGranted(context)

                                    if (allPermissionsGranted) {
                                        onStartSessionServer()
                                    } else {
                                        pendingStartSessionDialog = true
                                        permissionRequestLauncher.launch(NearbyPermissions.REQUIRED_PERMISSIONS)
                                    }
                                }
                            )
                        }
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                if (state.showProgressDialog) {
                    ProgressDialog(
                        message = state.progressMessage
                            ?: UiText.StringResource(R.string.progress_processing),
                        showOkButton = state.progressCompleted,
                        onDismiss = onHideProgressDialog
                    )
                }

                if (showImportDialog) {
                    ImportDialog(
                        onDismiss = {
                            showImportDialog = false
                            if (pendingImport != null) {
                                onClearPendingImport()
                            }
                        },
                        onImport = { importDialogState ->
                            showImportDialog = false
                            val options = ImportOptions(
                                deleteAll = importDialogState.deleteAll,
                                replaceOnConflict = importDialogState.replaceOnConflict
                            )

                            val currentPendingImport = pendingImport
                            if (currentPendingImport != null) {
                                // The file already sits in the cache: skip the file picker.
                                coroutineScope.launch {
                                    performImport(
                                        ImportInput.FromPendingImport(currentPendingImport),
                                        importDialogState.importFormat,
                                        options
                                    )
                                    onClearPendingImport()
                                }
                            } else {
                                pendingImportFormat = importDialogState.importFormat
                                pendingImportOptions = options
                                val mimeType = when (importDialogState.importFormat) {
                                    ImportFormat.LYRIC_CAST -> "application/zip"
                                    ImportFormat.OPEN_SONG -> "application/zip"
                                    else -> "*/*"
                                }
                                importLauncher.launch(mimeType)
                            }
                        },
                        pendingImportDisplayName = pendingImport?.displayName
                    )
                }

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
                        MainTab.SONGS -> songsContent()

                        MainTab.SETLISTS -> setlistsContent()

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

        // Overlaid rather than placed in the Scaffold's FAB slot so it clears the navigation
        // suite, which reserves no space for it. FloatingActionButtonMenu draws its own scrim
        // and handles outside-dismiss, so no manual blur/dim layer is needed.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = fabBottomPadding),
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
            songsState = dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.PreviewSongsScreenState,
            setlistsState = dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.PreviewSetlistsScreenState,
            onTabSelected = { },
            onShowProgressDialog = {},
            onHideProgressDialog = {},
            onExportAll = { },
            onHandleImport = { _, _, _ -> false },
            onNavigateToSettings = {},
            onSongsExitSelectionMode = {},
            onSongsDeleteSelected = {},
            onSongsExportSelected = { },
            onSetlistsExitSelectionMode = {},
            onSetlistsDeleteSelected = {},
            onSetlistsExportSelected = { }
        )
    }
}
