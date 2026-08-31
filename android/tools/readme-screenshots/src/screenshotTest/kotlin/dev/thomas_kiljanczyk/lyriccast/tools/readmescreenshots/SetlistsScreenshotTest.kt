package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.rememberScreenshotData
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainTab
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.SetlistSongItem
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor.MutableSetlistEditorState
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor.SetlistEditorScreen
import java.util.UUID

/** `LyricCast-setlists-1.png` and `LyricCast-setlists-2.png`. */
class SetlistsScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SetlistList() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                MainScreenshot(data, tab = MainTab.SETLISTS)
            }
        }
    }

    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SetlistEditor() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SetlistEditorScreen(
                    state = MutableSetlistEditorState().apply {
                        setlistName = data.setlistName
                        songs = listOf(
                            setlistSong(1, data.aSong),
                            setlistSong(2, data.awesomeSong),
                            setlistSong(3, data.greatSong),
                            // The same song twice -- the README calls out that a setlist can
                            // duplicate a song.
                            setlistSong(4, data.awesomeSong)
                        )
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
                    onMoveSong = { _, _ -> },
                    onRemoveSong = {},
                    onDuplicateSong = {},
                    onSaveSetlist = {},
                    onBackPressed = { false }
                )
            }
        }
    }
}

/**
 * Stable row ids: Layoutlib re-renders on every run, and the default random UUID would make the
 * shot's list keys differ from one render to the next for no visible gain.
 */
private fun setlistSong(index: Int, song: SongItem) =
    SetlistSongItem(song = song, id = UUID(3L, index.toLong()))
