/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 6:54 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 6:53 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.main.import_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastSpinner
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import kotlinx.collections.immutable.toImmutableList

@Stable
interface ImportDialogState {
    val importFormat: ImportFormat
    val deleteAll: Boolean
    val replaceOnConflict: Boolean
    val isReplaceOnConflictEnabled: Boolean
}

class MutableImportDialogState : ImportDialogState {
    override var importFormat by mutableStateOf(ImportFormat.LYRIC_CAST)
    override var deleteAll by mutableStateOf(false)
    override var replaceOnConflict by mutableStateOf(false)

    override val isReplaceOnConflictEnabled by derivedStateOf { !deleteAll }
}

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onImport: (ImportDialogState) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = remember { MutableImportDialogState() }

    ImportDialogStateless(
        state = state,
        onDismiss = onDismiss,
        onImport = onImport,
        onImportFormatChange = { state.importFormat = it },
        onDeleteAllChange = { checked ->
            state.deleteAll = checked
            // Clear replaceOnConflict if deleteAll is enabled
            if (checked) {
                state.replaceOnConflict = false
            }
        },
        onReplaceOnConflictChange = { state.replaceOnConflict = it },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDialogStateless(
    state: ImportDialogState,
    onDismiss: () -> Unit,
    onImport: (ImportDialogState) -> Unit,
    onImportFormatChange: (ImportFormat) -> Unit,
    onDeleteAllChange: (Boolean) -> Unit,
    onReplaceOnConflictChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableFormats = remember {
        ImportFormat.entries.filter { it != ImportFormat.NONE }.toImmutableList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.main_activity_import_dialog_title))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.widthIn(min = 300.dp)
            ) {
                // Import Format Spinner
                LyricCastSpinner(
                    options = availableFormats,
                    value = state.importFormat.displayName,
                    label = stringResource(R.string.hint_import_format),
                    onOptionSelected = onImportFormatChange,
                    optionContent = { format ->
                        Text(format.displayName)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = state.deleteAll,
                            onCheckedChange = onDeleteAllChange
                        )
                        Text(
                            text = stringResource(R.string.main_activity_import_delete_all_before_import),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = state.replaceOnConflict,
                            enabled = state.isReplaceOnConflictEnabled,
                            onCheckedChange = onReplaceOnConflictChange
                        )
                        Text(
                            text = stringResource(R.string.main_activity_import_replace_on_conflict),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onImport(state) }
            ) {
                Text(stringResource(R.string.main_activity_menu_import))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun ImportDialogPreview() {
    LyricCastTheme {
        Surface {
            val state = MutableImportDialogState()
            ImportDialogStateless(
                state = state,
                onDismiss = {},
                onImport = {},
                onImportFormatChange = { state.importFormat = it },
                onDeleteAllChange = {
                    state.deleteAll = it
                    if (it) state.replaceOnConflict = false
                },
                onReplaceOnConflictChange = { state.replaceOnConflict = it }
            )
        }
    }
}
