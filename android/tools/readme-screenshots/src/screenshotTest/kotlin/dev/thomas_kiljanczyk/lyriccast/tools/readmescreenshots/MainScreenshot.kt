package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.thomas_kiljanczyk.lyriccast.core.model.SetlistItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.ScreenshotData
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainScreen
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainTab
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MutableMainScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.PreviewSetlistsScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.SetlistsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.PreviewSongsScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.SongsScreen
import java.util.UUID
import kotlinx.collections.immutable.toImmutableList

/**
 * The app's home screen, on whichever tab a shot needs. Both tab bodies are passed explicitly --
 * their defaults reach for a Hilt-backed screen, which Layoutlib cannot build.
 */
@Composable
fun MainScreenshot(data: ScreenshotData, tab: MainTab = MainTab.SONGS) {
    MainScreen(
        state = MutableMainScreenState().apply { selectedTab = tab },
        songsState = PreviewSongsScreenState,
        setlistsState = PreviewSetlistsScreenState,
        onTabSelected = {},
        onShowProgressDialog = {},
        onHideProgressDialog = {},
        onExportAll = {},
        onHandleImport = { _, _, _ -> false },
        onNavigateToSettings = {},
        onSongsExitSelectionMode = {},
        onSongsDeleteSelected = {},
        onSongsExportSelected = {},
        onSetlistsExitSelectionMode = {},
        onSetlistsDeleteSelected = {},
        onSetlistsExportSelected = {},
        songsContent = {
            SongsScreen(
                state = PreviewSongsScreenState,
                songs = data.songs,
                categories = data.categories,
                onUpdateSearchQuery = {},
                onSelectCategory = {},
                onEnterSelectionMode = {},
                onToggleSongSelection = {}
            )
        },
        setlistsContent = {
            SetlistsScreen(
                state = PreviewSetlistsScreenState,
                setlists = demoSetlists(data),
                snackbarHostState = remember { SnackbarHostState() },
                onUpdateSearchQuery = {},
                onEnterSelectionMode = {},
                onToggleSetlistSelection = {}
            )
        }
    )
}

/**
 * Only the first name comes from the shared demo data, which carries a single setlist; the other
 * two are plain English, which this module can afford -- unlike the app itself, the README is only
 * ever produced in one language. Song titles would not do: a list of setlists named after songs
 * reads as the wrong screen.
 */
private fun demoSetlists(data: ScreenshotData) = listOf(
    SetlistItem(
        id = setlistId(1),
        name = data.setlistName,
        presentation = data.songs.take(4).toImmutableList()
    ),
    SetlistItem(
        id = setlistId(2),
        name = "Another Setlist",
        presentation = data.songs.take(2).toImmutableList()
    ),
    SetlistItem(
        id = setlistId(3),
        name = "My Favorite Setlist",
        presentation = data.songs.take(3).toImmutableList()
    )
)

private fun setlistId(index: Int) = UUID(2L, index.toLong())
