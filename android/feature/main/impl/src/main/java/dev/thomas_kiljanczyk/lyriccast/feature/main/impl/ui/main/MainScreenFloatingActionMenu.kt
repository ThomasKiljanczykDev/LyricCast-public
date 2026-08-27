/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 5:35 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 5:35 PM
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
    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                checked = expanded,
                onCheckedChange = onExpandedChange,
                // Keeps the toggle ahead of the menu items in accessibility traversal order.
                modifier = Modifier.semantics { traversalIndex = -1f }
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
            icon = { Icon(Icons.AutoMirrored.Rounded.PlaylistAdd, contentDescription = null) },
            text = { Text(stringResource(R.string.main_activity_button_add_setlist)) }
        )

        FloatingActionButtonMenuItem(
            onClick = {
                onAddSong()
                onExpandedChange(false)
            },
            icon = { Icon(Icons.Rounded.MusicNote, contentDescription = null) },
            text = { Text(stringResource(R.string.main_activity_button_add_song)) }
        )
    }
}
