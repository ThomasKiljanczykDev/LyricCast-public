/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.gplayscreenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainScreenTopBar
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.main.MainTab
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.PreviewSongsScreenState
import dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs.SongsScreen
import kotlinx.collections.immutable.toImmutableList

class SongsScreenshotTest {
    @OptIn(ExperimentalMaterial3Api::class)
    @PreviewTest
    @StoreScreenshots
    @Composable
    fun Songs() {
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                Scaffold(
                    topBar = {
                        MainScreenTopBar(
                            onNavigateToCategoryManager = {},
                            onImport = {},
                            onExport = {},
                            onNavigateToSettings = {},
                            onStartSession = {}
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            MainTab.entries.forEach { tab ->
                                NavigationBarItem(
                                    selected = tab == MainTab.SONGS,
                                    onClick = {},
                                    icon = { Icon(tab.icon, contentDescription = null) },
                                    label = { Text(stringResource(tab.titleRes)) }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        SongsScreen(
                            state = PreviewSongsScreenState,
                            songs = PreviewData.sampleSongs,
                            categories = PreviewData.sampleCategoriesWithNull.toImmutableList(),
                            onUpdateSearchQuery = {},
                            onSelectCategory = {},
                            onEnterSelectionMode = {},
                            onToggleSongSelection = {}
                        )
                    }
                }
            }
        }
    }
}
