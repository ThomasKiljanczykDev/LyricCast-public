/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:05 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:58 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.tourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.SwipeToRevealBox
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.SetlistSongItem
import java.util.UUID
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun SetlistEditorSongList(
    songs: List<SetlistSongItem>,
    isInSelectionMode: Boolean,
    onMoveSong: (Int, Int) -> Unit,
    onSelectSong: (UUID, Boolean) -> Unit,
    onRemoveSong: (Int) -> Unit,
    onDuplicateSong: (Int) -> Unit,
    onToggleSelectionMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onMoveSong(from.index, to.index)
    }
    val hapticFeedback = LocalHapticFeedback.current

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .tourAnchor(TourAnchor.SETLIST_EDITOR_SONG_PICKER),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items = songs, key = { _, item -> item.id }) { index, songItem ->
            ReorderableItem(reorderableLazyListState, key = songItem.id) { isDragging ->
                SetlistSongItem(
                    draggableHandleModifier = Modifier.draggableHandle(onDragStarted = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                    }, onDragStopped = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                    }),
                    songItem = songItem,
                    isInSelectionMode = isInSelectionMode,
                    isDragging = isDragging,
                    onToggleSelection = { selected ->
                        onSelectSong(songItem.id, selected)
                    },
                    onLongPress = {
                        if (!isInSelectionMode) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onToggleSelectionMode()
                            onSelectSong(songItem.id, true)
                        }
                    },
                    onDelete = {
                        onRemoveSong(index)
                    },
                    onDuplicate = {
                        onDuplicateSong(index)
                    })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SetlistSongItem(
    songItem: SetlistSongItem,
    isInSelectionMode: Boolean,
    isDragging: Boolean,
    onToggleSelection: (Boolean) -> Unit,
    onLongPress: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier,
    draggableHandleModifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (songItem.isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else Color.Transparent, label = "border_color"
    )

    SwipeToRevealBox(
        modifier = modifier,
        enableSwipe = !isInSelectionMode,
        onSwipeLeft = onDelete,
        onSwipeRight = onDuplicate,
        leftIcon = Icons.Rounded.Delete,
        rightIcon = Icons.Rounded.Queue
    ) {
        // Main card content
        Card(
            modifier = Modifier
                .zIndex(if (isDragging) 1f else 0f)
                .fillMaxWidth()
                .border(
                    width = 3.dp, color = animatedBorderColor, shape = CardDefaults.shape
                )
                .clip(CardDefaults.shape)
                .combinedClickable(
                    onClick = {
                        if (isInSelectionMode) {
                            onToggleSelection(!songItem.isSelected)
                        }
                    }, onLongClick = onLongPress
                )
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = songItem.song.title, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, trailingContent = {
                    AnimatedVisibility(
                        visible = !isInSelectionMode, enter = fadeIn(), exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DragHandle,
                            contentDescription = "Drag to reorder",
                            modifier = draggableHandleModifier
                                .clip(CardDefaults.shape)
                                .clickable {
                                    /* Suppress click propagation */
                                })
                    }
                }, colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SetlistSongItemPreview_Normal() {
    LyricCastTheme {
        SetlistSongItem(
            songItem = SetlistSongItem(
                song = SongItem(
                    id = UUID.randomUUID(),
                    title = "Amazing Grace",
                    lyricsMap = persistentMapOf(),
                    presentation = persistentListOf(),
                    category = null
                ),
                isSelected = false
            ),
            isInSelectionMode = false,
            isDragging = false,
            onToggleSelection = { },
            onLongPress = { },
            onDelete = { },
            onDuplicate = { },
            draggableHandleModifier = Modifier
        )
    }
}

@PreviewLightDark
@Composable
private fun SetlistSongItemPreview_Selected() {
    LyricCastTheme {
        Surface {
            SetlistSongItem(
                songItem = SetlistSongItem(
                    song = SongItem(
                        id = UUID.randomUUID(),
                        title = "How Great Thou Art",
                        lyricsMap = persistentMapOf(),
                        presentation = persistentListOf(),
                        category = null
                    ),
                    isSelected = true
                ),
                isInSelectionMode = true,
                isDragging = false,
                onToggleSelection = { },
                onLongPress = { },
                onDelete = { },
                onDuplicate = { },
                draggableHandleModifier = Modifier
            )
        }
    }
}
