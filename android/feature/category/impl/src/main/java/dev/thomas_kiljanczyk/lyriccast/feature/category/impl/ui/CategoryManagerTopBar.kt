/*
 * Created by Tomasz Kiljanczyk on 9/2/25, 9:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/2/25, 9:03 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.R as CoreUiR
import dev.thomas_kiljanczyk.lyriccast.core.ui.testing.TestTags
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerTopBar(
    selectedCount: Int,
    onAdd: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onCancelSelection: () -> Unit = {},
    onNavigateUp: () -> Unit = {}
) {
    val hasSelection = selectedCount > 0

    Crossfade(
        targetState = hasSelection,
        label = "topbar_crossfade"
    ) { inSelectionMode ->
        if (inSelectionMode) {
            // Selection Mode TopBar (inlined)
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onCancelSelection) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(android.R.string.cancel)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = selectedCount == 1,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = onEdit) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = stringResource(CoreUiR.string.edit)
                            )
                        }
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteOutline,
                            contentDescription = stringResource(CoreUiR.string.delete)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        } else {
            // Normal Mode TopBar (inlined)
            TopAppBar(
                title = { Text(stringResource(R.string.title_categories)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(CoreUiR.string.navigate_up)
                        )
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = onAdd,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag(TestTags.ADD_CATEGORY_BUTTON)
                    ) {
                        Text(stringResource(R.string.editor_button_add))
                    }
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryManagerTopBarPreview_NotHasSelection() {
    LyricCastTheme {
        Surface {
            CategoryManagerTopBar(selectedCount = 0)
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryManagerTopBarPreview_HasSingleSelection() {
    LyricCastTheme {
        Surface {
            CategoryManagerTopBar(selectedCount = 1)
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryManagerTopBarPreview_HasMultipleSelections() {
    LyricCastTheme {
        Surface {
            CategoryManagerTopBar(selectedCount = 5)
        }
    }
}
