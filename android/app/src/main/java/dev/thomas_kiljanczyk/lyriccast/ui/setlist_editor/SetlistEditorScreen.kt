/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.setlist_editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.shared.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.ui.setlist_editor.songs.SetlistSongSelectionDialog
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.LyricCastTextField
import dev.thomas_kiljanczyk.lyriccast.ui.shared.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.ui.shared.theme.LyricCastTheme
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SetlistEditorScreen(
    setlistId: UUID? = null,
    presentation: List<UUID>? = null,
    onNavigateBack: () -> Unit,
    viewModel: SetlistEditorViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSongSelectionDialog by remember { mutableStateOf(false) }

    // Load setlist data
    LaunchedEffect(setlistId, presentation) {
        when {
            setlistId != null && presentation != null -> {
                viewModel.loadSetlist(setlistId, presentation)
            }

            setlistId != null -> {
                viewModel.loadEditedSetlist(setlistId)
            }

            presentation != null -> {
                viewModel.loadAdhocSetlist(presentation)
            }
        }
    }

    // Show song selection dialog
    if (showSongSelectionDialog) {
        SetlistSongSelectionDialog(
            onDismiss = { showSongSelectionDialog = false },
            onConfirm = { selectedSongIds ->
                coroutineScope.launch {
                    viewModel.updateSongsFromSelection(selectedSongIds)
                    showSongSelectionDialog = false
                }
            },
            currentSongIds = viewModel.getSetlistSongIds()
        )
    }

    SetlistEditorScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onNavigateToSongSelection = { showSongSelectionDialog = true },
        onSetlistNameChanged = viewModel::setSetlistName,
        onToggleSelectionMode = viewModel::toggleSelectionMode,
        onSelectSong = viewModel::selectSong,
        onRemoveSelectedSongs = viewModel::removeSelectedSongs,
        onDuplicateSelectedSongs = viewModel::duplicateSelectedSongs,
        onMoveSong = viewModel::moveSong,
        onRemoveSong = viewModel::removeSongAt,
        onDuplicateSong = viewModel::duplicateSongAt,
        onSaveSetlist = {
            coroutineScope.launch {
                viewModel.saveSetlist()
                onNavigateBack()
            }
        },
        onBackPressed = viewModel::onBackPressed
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SetlistEditorScreen(
    state: SetlistEditorState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onNavigateToSongSelection: () -> Unit,
    onSetlistNameChanged: (String) -> Unit,
    onToggleSelectionMode: () -> Unit,
    onSelectSong: (UUID, Boolean) -> Unit,
    onRemoveSelectedSongs: () -> Unit,
    onDuplicateSelectedSongs: () -> Unit,
    onMoveSong: (Int, Int) -> Unit,
    onRemoveSong: (Int) -> Unit,
    onDuplicateSong: (Int) -> Unit,
    onSaveSetlist: () -> Unit,
    onBackPressed: () -> Boolean
) {
    BackHandler(enabled = state.isInSelectionMode) {
        onBackPressed()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        SetlistEditorTopBar(
            isInSelectionMode = state.isInSelectionMode,
            selectedCount = state.selectedSongs.size,
            canSave = state.canSave,
            onNavigateBack = onNavigateBack,
            onSaveSetlist = onSaveSetlist,
            onToggleSelectionMode = onToggleSelectionMode,
            onRemoveSelectedSongs = onRemoveSelectedSongs,
            onDuplicateSelectedSongs = onDuplicateSelectedSongs,
            scrollBehavior = scrollBehavior
        )
    }, floatingActionButton = {
        AnimatedVisibility(!state.isInSelectionMode, enter = fadeIn(), exit = fadeOut()) {
            FloatingActionButton(
                onClick = onNavigateToSongSelection
            ) {
                Icon(
                    Icons.Rounded.Add, contentDescription = stringResource(R.string.add_song)
                )
            }
        }
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Setlist name input
            LyricCastTextField(
                value = state.setlistName,
                onValueChange = { newValue ->
                    if (newValue.length <= 30) {
                        onSetlistNameChanged(newValue)
                    }
                },
                label = stringResource(R.string.setlist_name),
                maxLength = 30,
                errorText = when (state.nameValidationState) {
                    NameValidationState.EMPTY -> stringResource(R.string.error_name_empty)
                    NameValidationState.ALREADY_IN_USE -> stringResource(R.string.error_name_already_in_use)
                    else -> null
                },
                containerModifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 4.dp, top = 8.dp),
                singleLine = true
            )

            SetlistEditorSongList(
                songs = state.songs,
                isInSelectionMode = state.isInSelectionMode,
                onMoveSong = onMoveSong,
                onSelectSong = onSelectSong,
                onRemoveSong = { index ->
                    onRemoveSong(index)
                },
                onDuplicateSong = onDuplicateSong,
                onToggleSelectionMode = onToggleSelectionMode
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetlistEditorTopBar(
    isInSelectionMode: Boolean,
    selectedCount: Int,
    canSave: Boolean,
    onNavigateBack: () -> Unit,
    onSaveSetlist: () -> Unit,
    onToggleSelectionMode: () -> Unit,
    onRemoveSelectedSongs: () -> Unit,
    onDuplicateSelectedSongs: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Crossfade(
        targetState = isInSelectionMode, label = "topbar_crossfade"
    ) { inSelectionMode ->
        if (inSelectionMode) {
            // Selection Mode TopBar (inlined)
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onToggleSelectionMode) {
                        Icon(
                            Icons.Rounded.Close, contentDescription = stringResource(R.string.close)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = selectedCount == 1, enter = fadeIn(), exit = fadeOut()
                    ) {
                        IconButton(onClick = onDuplicateSelectedSongs) {
                            Icon(
                                Icons.Rounded.Queue,
                                contentDescription = stringResource(R.string.duplicate)
                            )
                        }
                    }
                    IconButton(
                        onClick = onRemoveSelectedSongs, enabled = selectedCount > 0
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        } else {
            // Normal Mode TopBar (inlined)
            TopAppBar(
                title = { Text(stringResource(R.string.setlist_editor_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onSaveSetlist,
                        enabled = canSave,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun SetlistEditorScreenPreview() {
    LyricCastTheme {
        SetlistEditorScreen(
            state = MutableSetlistEditorState().apply {
                setlistName = "Sunday Service"
                songs = PreviewData.sampleSetlistSongItems
                isInSelectionMode = true
                nameValidationState = NameValidationState.VALID
            },
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onNavigateToSongSelection = {},
            onSetlistNameChanged = {},
            onToggleSelectionMode = {},
            onSelectSong = { _, _ -> },
            onRemoveSelectedSongs = {},
            onDuplicateSelectedSongs = {},
            onRemoveSong = {},
            onDuplicateSong = {},
            onMoveSong = { _, _ -> },
            onSaveSetlist = {},
            onBackPressed = { false })
    }
}