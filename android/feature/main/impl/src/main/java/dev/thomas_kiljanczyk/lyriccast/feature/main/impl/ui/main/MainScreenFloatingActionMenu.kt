/*
 * Created by Tomasz Kiljanczyk on 8/5/26, 2:30 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/5/26, 1:08 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.LocalTourExpansion
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourExpandable
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.tourAnchor
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreenFloatingActionMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAddSong: () -> Unit,
    onAddSetlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val effectiveExpanded =
        expanded || LocalTourExpansion.current.isForcedOpen(TourExpandable.MAIN_FAB_MENU)

    FloatingActionButtonMenu(
        // Anchored on the menu so the spotlight covers the toggle and its items.
        modifier = modifier.tourAnchor(TourAnchor.MAIN_FAB_MENU),
        expanded = effectiveExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = effectiveExpanded,
                onCheckedChange = onExpandedChange,
                // Keeps the toggle ahead of the menu items in accessibility traversal order.
                modifier = Modifier
                    .semantics { traversalIndex = -1f }
                    .tourAnchor(TourAnchor.MAIN_FAB)
            ) {
                val icon by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Rounded.Close else Icons.Rounded.Add
                    }
                }

                Icon(
                    painter = rememberVectorPainter(icon),
                    contentDescription = stringResource(R.string.main_activity_button_add),
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                onAddSetlist()
                onExpandedChange(false)
            },
            modifier = Modifier.tourAnchor(TourAnchor.MAIN_FAB_ADD_SETLIST),
            icon = { Icon(Icons.AutoMirrored.Rounded.PlaylistAdd, contentDescription = null) },
            text = { Text(stringResource(R.string.main_activity_button_add_setlist)) }
        )

        FloatingActionButtonMenuItem(
            onClick = {
                onAddSong()
                onExpandedChange(false)
            },
            modifier = Modifier.tourAnchor(TourAnchor.MAIN_FAB_ADD_SONG),
            icon = { Icon(Icons.Rounded.MusicNote, contentDescription = null) },
            text = { Text(stringResource(R.string.main_activity_button_add_song)) }
        )
    }
}
