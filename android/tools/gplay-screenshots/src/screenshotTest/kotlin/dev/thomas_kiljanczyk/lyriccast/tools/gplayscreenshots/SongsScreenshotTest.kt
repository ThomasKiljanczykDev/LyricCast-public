/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.gplayscreenshots

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainScreen
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MutableMainScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists.MutableSetlistsScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.MutableSongsScreenState

class SongsScreenshotTest {
    @PreviewTest
    @StoreScreenshots
    @Composable
    fun Songs() {
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                MainScreen(
                    state = MutableMainScreenState(),
                    songsState = MutableSongsScreenState().apply {
                        songs = PreviewData.sampleSongs
                    },
                    setlistsState = MutableSetlistsScreenState().apply {
                        allSetlists = PreviewData.sampleSetlists
                    },
                    onTabSelected = {},
                    onShowProgressDialog = {},
                    onHideProgressDialog = {},
                    onExportAll = { _, _ -> },
                    onHandleImport = { _, _, _, _ -> false },
                    onNavigateToSettings = {},
                    onSongsExitSelectionMode = {},
                    onSongsDeleteSelected = {},
                    onSongsExportSelected = { _, _ -> },
                    onSetlistsExitSelectionMode = {},
                    onSetlistsDeleteSelected = {},
                    onSetlistsExportSelected = { _, _ -> }
                )
            }
        }
    }
}
