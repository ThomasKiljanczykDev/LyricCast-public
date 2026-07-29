/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 4:29 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 4:04 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PresentToAll
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.ui.shared.components.CastButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopBar(
    onNavigateToCategoryManager: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onStartSession: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            // Start session button
            IconButton(onClick = onStartSession) {
                Icon(
                    Icons.Rounded.PresentToAll,
                    contentDescription = stringResource(R.string.dialog_fragment_start_session_title)
                )
            }

            // Cast button - always visible
            CastButton(size = 48.dp)

            // Three-dots menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.more_options)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_title)) },
                        onClick = {
                            showMenu = false
                            onNavigateToSettings()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Settings,
                                contentDescription = null
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.title_category_manager)) },
                        onClick = {
                            showMenu = false
                            onNavigateToCategoryManager()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Category,
                                contentDescription = null
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.main_activity_menu_import)) },
                        onClick = {
                            showMenu = false
                            onImport()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.ImportExport,
                                contentDescription = null
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.main_activity_menu_export)) },
                        onClick = {
                            showMenu = false
                            onExport()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.UploadFile,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SongsActionModeTopBar(
    selectedCount: Int,
    onExitSelectionMode: () -> Unit,
    onEditSong: () -> Unit,
    onAddToSetlist: () -> Unit,
    onExportSongs: () -> Unit,
    onDeleteSongs: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onExitSelectionMode) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        },
        actions = {
            // Show edit button only when exactly one song is selected
            AnimatedVisibility(
                visible = selectedCount == 1,
                enter = fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                exit = fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec())
            ) {
                IconButton(onClick = onEditSong) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }

            IconButton(onClick = onAddToSetlist) {
                Icon(
                    Icons.AutoMirrored.Rounded.PlaylistAdd,
                    contentDescription = stringResource(R.string.main_activity_button_add_setlist)
                )
            }

            IconButton(onClick = onExportSongs) {
                Icon(
                    Icons.Rounded.UploadFile,
                    contentDescription = stringResource(R.string.main_activity_action_menu_export_selected)
                )
            }

            IconButton(onClick = onDeleteSongs) {
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetlistsActionModeTopBar(
    selectedCount: Int,
    onExitSelectionMode: () -> Unit,
    onEditSetlist: () -> Unit,
    onExportSetlists: () -> Unit,
    onDeleteSetlists: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onExitSelectionMode) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        },
        actions = {
            // Show edit button only when exactly one setlist is selected
            AnimatedVisibility(
                visible = selectedCount == 1,
                enter = fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()),
                exit = fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec())
            ) {
                IconButton(onClick = onEditSetlist) {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }

            IconButton(onClick = onExportSetlists) {
                Icon(
                    Icons.Rounded.UploadFile,
                    contentDescription = stringResource(R.string.main_activity_action_menu_export_selected)
                )
            }

            IconButton(onClick = onDeleteSetlists) {
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
}