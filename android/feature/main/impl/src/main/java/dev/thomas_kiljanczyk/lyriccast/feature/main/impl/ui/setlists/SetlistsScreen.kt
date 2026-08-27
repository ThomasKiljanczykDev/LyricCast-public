/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 7:11 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 6:15 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.SetlistItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastTextField
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.R
import java.util.UUID
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistsScreen(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavigateToSetlistControls: (UUID) -> Unit = {},
    viewModel: SetlistsScreenViewModel = hiltViewModel(
        viewModelStoreOwner = LocalActivity.current!! as ViewModelStoreOwner
    )
) {
    val state = viewModel.state

    SetlistsScreen(
        state = state,
        setlists = state.setlists,
        snackbarHostState = snackbarHostState,
        onUpdateSearchQuery = viewModel::updateSearchQuery,
        onEnterSelectionMode = viewModel::enterSelectionMode,
        onToggleSetlistSelection = viewModel::toggleSetlistSelection,
        onNavigateToSetlistControls = onNavigateToSetlistControls
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistsScreen(
    state: SetlistsScreenState,
    setlists: List<SetlistItem>,
    snackbarHostState: SnackbarHostState,
    onUpdateSearchQuery: (String) -> Unit,
    onEnterSelectionMode: () -> Unit,
    onToggleSetlistSelection: (SetlistItem) -> Unit,
    onNavigateToSetlistControls: (UUID) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search filter
        LyricCastTextField(
            value = state.searchQuery,
            onValueChange = onUpdateSearchQuery,
            label = stringResource(R.string.hint_setlist_name),
            singleLine = true,
            containerModifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 4.dp,
                top = 8.dp
            )
        )

        // Setlists list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(items = setlists, key = { it.id }) { setlistItem ->
                val setlistIsEmptyString = stringResource(R.string.main_activity_setlist_is_empty)
                SetlistItem(
                    modifier = Modifier.animateItem(),
                    setlistItem = setlistItem, onClick = {
                        if (state.isInSelectionMode) {
                            onToggleSetlistSelection(setlistItem)
                        } else {
                            if (setlistItem.presentation.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(setlistIsEmptyString)
                                }
                                return@SetlistItem
                            }

                            onNavigateToSetlistControls(setlistItem.id)
                        }
                    }, onLongClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (!state.isInSelectionMode) {
                            onEnterSelectionMode()
                        }
                        onToggleSetlistSelection(setlistItem)
                    })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SetlistItem(
    setlistItem: SetlistItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (setlistItem.isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else Color.Transparent, label = "border_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 3.dp, color = animatedBorderColor, shape = CardDefaults.shape
            )
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = onClick, onLongClick = onLongClick
            ), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = setlistItem.name, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }, supportingContent = {
                Row {
                    Text(
                        text = "${setlistItem.presentation.size} songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }, colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun SetlistsScreenPreview() {
    LyricCastTheme {
        Surface {
            SetlistsScreen(
                state = MutableSetlistsScreenState(),
                setlists = PreviewData.sampleSetlists,
                snackbarHostState = remember { SnackbarHostState() },
                onUpdateSearchQuery = {},
                onEnterSelectionMode = {},
                onToggleSetlistSelection = {}
            )
        }
    }
}
