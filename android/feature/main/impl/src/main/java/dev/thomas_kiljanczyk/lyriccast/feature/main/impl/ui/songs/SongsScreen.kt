/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:03 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongFilters
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    onNavigateToSongControls: (UUID) -> Unit = {},
    viewModel: SongsScreenViewModel = hiltViewModel(
        viewModelStoreOwner = LocalActivity.current!! as ViewModelStoreOwner
    )
) {
    val state = viewModel.state
    val categories by viewModel.categories.collectAsState(initial = emptyList())

    SongsScreen(
        state = state,
        songs = state.filteredSongs,
        categories = categories.map { CategoryItem(it) }.let { listOf(null) + it }
            .toImmutableList(),
        onUpdateSearchQuery = viewModel::updateSearchQuery,
        onSelectCategory = viewModel::selectCategory,
        onEnterSelectionMode = viewModel::enterSelectionMode,
        onToggleSongSelection = viewModel::toggleSongSelection,
        onNavigateToSongControls = onNavigateToSongControls
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    state: SongsScreenState,
    songs: List<SongItem>,
    categories: ImmutableList<CategoryItem?>,
    onUpdateSearchQuery: (String) -> Unit,
    onSelectCategory: (CategoryItem?) -> Unit,
    onEnterSelectionMode: () -> Unit,
    onToggleSongSelection: (SongItem) -> Unit,
    onNavigateToSongControls: (UUID) -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Filters
        SongFilters(
            state = state.filterState,
            onSearchTextChanged = onUpdateSearchQuery,
            categories = categories,
            onCategorySelected = onSelectCategory,
            showSelectedFilter = false
        )

        // Songs list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(
                items = songs,
                key = { it.id }
            ) { songItem ->
                SongItem(
                    songItem = songItem,
                    onToggleSelection = {
                        if (state.isInSelectionMode) {
                            onToggleSongSelection(songItem)
                        } else {
                            onNavigateToSongControls(songItem.id)
                        }
                    },
                    onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (!state.isInSelectionMode) {
                            onEnterSelectionMode()
                        }
                        onToggleSongSelection(songItem)
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SongsScreenPreview() {
    LyricCastTheme {
        Surface {
            SongsScreen(
                state = MutableSongsScreenState(),
                songs = PreviewData.sampleSongs.take(3),
                categories = PreviewData.sampleCategoriesWithNull.toImmutableList(),
                onUpdateSearchQuery = {},
                onSelectCategory = {},
                onEnterSelectionMode = {},
                onToggleSongSelection = {}
            )
        }
    }
}
