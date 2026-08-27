/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 5:33 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 5:23 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.R
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.SongFilterState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SongFilters(
    state: SongFilterState,
    onSearchTextChanged: (String) -> Unit,
    categories: ImmutableList<CategoryItem?>,
    onCategorySelected: (CategoryItem?) -> Unit,
    modifier: Modifier = Modifier,
    onShowOnlySelectedToggle: () -> Unit = {},
    showSelectedFilter: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search field
        LyricCastTextField(
            value = state.searchText,
            onValueChange = onSearchTextChanged,
            label = stringResource(R.string.hint_song_title),
            singleLine = true
        )

        // Category dropdown and optional selected switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category dropdown
            CategoryDropdown(
                categories = categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.weight(1f)
            )

            // Selected filter chip (only shown when requested)
            if (showSelectedFilter) {
                FilterChip(
                    selected = state.showOnlySelected,
                    onClick = onShowOnlySelectedToggle,
                    label = { Text(stringResource(R.string.setlist_editor_hint_selected)) }
                )
            }
        }
    }
}
