/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 5:35 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 5:35 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreenFloatingActionMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAddSong: () -> Unit,
    onAddSetlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // FAB Options
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Add Setlist FAB
                SmallFloatingActionButton(
                    onClick = {
                        onAddSetlist()
                        onExpandedChange(false)
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.PlaylistAdd,
                            contentDescription = stringResource(R.string.main_activity_button_add_setlist),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.main_activity_button_add_setlist),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Add Song FAB
                SmallFloatingActionButton(
                    onClick = {
                        onAddSong()
                        onExpandedChange(false)
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.MusicNote,
                            contentDescription = stringResource(R.string.main_activity_button_add_song),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.main_activity_button_add_song),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) },
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            val rotation by animateFloatAsState(
                targetValue = if (expanded) 45f else 0f,
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                label = "fab_rotation"
            )

            Icon(
                Icons.Rounded.Add,
                contentDescription = stringResource(R.string.main_activity_button_add_song),
                modifier = Modifier
                    .size(28.dp)
                    .rotate(rotation)
            )
        }
    }
}
