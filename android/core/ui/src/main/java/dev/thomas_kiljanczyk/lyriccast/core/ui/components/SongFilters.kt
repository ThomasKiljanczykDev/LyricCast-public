/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 5:33 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 5:23 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.R
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.SongFilterState
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.flowItemMinWidth
import kotlinx.collections.immutable.ImmutableList

/**
 * Minimum width each flexible filter field (search / category) keeps. It is the field's line-break
 * footprint inside the [FlowRow]: an item only stays on a line when its minimum width still fits, so
 * the two fields share a row only when the measured width can give each this much. Below that they
 * wrap and each weighted item fills the width of whatever row it lands on. Applied through
 * [flowItemMinWidth] because a Material `TextField`'s own 280 dp `defaultMinSize` would otherwise
 * force a wider break point than we want.
 */
private val FILTER_FIELD_MIN_WIDTH = 220.dp

/** Horizontal gap between items sharing a row, and vertical gap between wrapped rows. */
private val FILTER_SPACING = 12.dp

/**
 * Search / category / filter-pill controls laid out in a single self-wrapping [FlowRow]. Everything
 * sits on one row when the width allows; as space tightens the category drops below the search
 * field, and the pill wraps to its own row — measured against the real available width rather than
 * a window-size-class breakpoint, so the same rule covers phones, foldables and tablets in any
 * orientation.
 */
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
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(FILTER_SPACING),
        verticalArrangement = Arrangement.spacedBy(FILTER_SPACING),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        LyricCastTextField(
            value = state.searchText,
            onValueChange = onSearchTextChanged,
            label = stringResource(R.string.hint_song_title),
            singleLine = true,
            containerModifier = Modifier
                .weight(1f)
                .flowItemMinWidth(FILTER_FIELD_MIN_WIDTH)
        )

        CategoryDropdown(
            categories = categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected,
            modifier = Modifier
                .weight(1f)
                .flowItemMinWidth(FILTER_FIELD_MIN_WIDTH)
        )

        // No weight: the pill takes only the width it needs, so it joins the fields' row when
        // that row has room and otherwise drops to its own.
        if (showSelectedFilter) {
            SelectedFilterChip(
                selected = state.showOnlySelected,
                label = stringResource(R.string.setlist_editor_hint_selected),
                onToggle = onShowOnlySelectedToggle
            )
        }
    }
}

/**
 * The "show only selected" filter pill. Wrapped so the minimum interactive-component size is
 * unset, letting the chip keep its natural (~32 dp) height instead of being padded out to the
 * 48 dp touch target, which would otherwise dictate the height of the whole filter row.
 */
@Composable
private fun SelectedFilterChip(
    selected: Boolean,
    label: String,
    onToggle: () -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        FilterChip(
            selected = selected,
            onClick = onToggle,
            label = { Text(label) }
        )
    }
}
