/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:34 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.setlist_editor.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.color.BaseColors
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongFilters
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongItem
import java.util.UUID
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun SetlistSongSelectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<UUID>) -> Unit,
    currentSongIds: List<UUID> = emptyList(),
    viewModel: SetlistSongSelectionDialogViewModel = hiltViewModel()
) {
    val state = viewModel.state

    // Set initial selection when dialog opens
    LaunchedEffect(currentSongIds) {
        viewModel.setInitialSelection(currentSongIds)
    }

    SetlistSongSelectionDialog(
        state = state,
        onDismiss = onDismiss,
        onConfirm = {
            val newSelectedSongIds = viewModel.getSelectedSongIds()
            onConfirm(newSelectedSongIds)
        },
        onCategorySelected = viewModel::updateSelectedCategory,
        onSearchTextChanged = viewModel::updateSearchText,
        onShowOnlySelectedToggle = { viewModel.updateShowOnlySelected(!state.filterState.showOnlySelected) },
        onSongToggle = viewModel::selectAvailableSong
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistSongSelectionDialog(
    state: SetlistSongSelectionDialogState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCategorySelected: (CategoryItem?) -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onShowOnlySelectedToggle: () -> Unit,
    onSongToggle: (SongItem) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.setlist_editor_button_pick_songs)) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = onConfirm, modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(R.string.confirm))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Search and filter section
                    SongFilters(
                        state = state.filterState,
                        onSearchTextChanged = onSearchTextChanged,
                        categories = state.categories,
                        onCategorySelected = onCategorySelected,
                        onShowOnlySelectedToggle = onShowOnlySelectedToggle,
                        showSelectedFilter = true
                    )

                    // Songs list
                    if (state.filteredAvailableSongs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_songs_found),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.filteredAvailableSongs,
                                key = { it.id }) { songItem ->
                                SongItem(
                                    songItem = songItem,
                                    onToggleSelection = {
                                        onSongToggle(songItem)
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SetlistSongSelectionDialogPreview() {
    LyricCastTheme {
        val sampleSongs = listOf(
            SongItem.fromSong(
                Song(
                    title = "Amazing Grace",
                    lyrics = emptyList(),
                    presentation = emptyList(),
                    category = null
                ), true
            ), SongItem.fromSong(
                Song(
                    title = "How Great Thou Art",
                    lyrics = emptyList(),
                    presentation = emptyList(),
                    category = Category(
                        name = "Hymns", color = BaseColors.Tomato
                    )
                ), false
            ), SongItem.fromSong(
                Song(
                    title = "Be Still My Soul",
                    lyrics = emptyList(),
                    presentation = emptyList(),
                    category = null
                ), false
            )
        )

        val sampleCategories = listOf(
            CategoryItem(
                Category(
                    name = "Hymns"
                )
            )
        )

        val sampleState = MutableSetlistSongSelectionDialogState().apply {
            allAvailableSongs = sampleSongs.toPersistentList()
            categories = sampleCategories.toImmutableList()
            filterState.selectedCategory = null
            filterState.searchText = ""
            filterState.showOnlySelected = false
        }

        // Use the props-only version for preview
        Surface {
            SetlistSongSelectionDialog(
                state = sampleState,
                onDismiss = {},
                onConfirm = {},
                onCategorySelected = {},
                onSearchTextChanged = {},
                onShowOnlySelectedToggle = {},
                onSongToggle = {})
        }
    }
}
