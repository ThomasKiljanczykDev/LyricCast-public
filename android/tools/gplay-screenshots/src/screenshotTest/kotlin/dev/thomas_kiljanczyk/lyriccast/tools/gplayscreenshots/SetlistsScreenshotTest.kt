/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.gplayscreenshots

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor.MutableSetlistEditorState
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor.SetlistEditorScreen
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.shared.preview.SetlistPreviewData

class SetlistsScreenshotTest {
    @PreviewTest
    @StoreScreenshots
    @Composable
    fun Setlists() {
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SetlistEditorScreen(
                    state = MutableSetlistEditorState().apply {
                        setlistName = "Sunday Service"
                        songs = SetlistPreviewData.sampleSetlistSongItems
                        isInSelectionMode = true
                        nameValidationState = NameValidationState.VALID
                    },
                    snackbarHostState = remember { SnackbarHostState() },
                    onNavigateBack = {},
                    onNavigateToSongSelection = {},
                    onSetlistNameChanged = {},
                    onToggleSelectionMode = {},
                    onSelectSong = { _, _ -> },
                    onRemoveSelectedSongs = {},
                    onDuplicateSelectedSongs = {},
                    onRemoveSong = {},
                    onDuplicateSong = {},
                    onMoveSong = { _, _ -> },
                    onSaveSetlist = {},
                    onBackPressed = { false }
                )
            }
        }
    }
}
