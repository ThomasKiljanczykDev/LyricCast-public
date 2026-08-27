/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.rememberScreenshotData
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor.MutableSongEditorState
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor.SongEditorScreen

private const val VERSE_NAME = "Verse 1"
private const val CHORUS_NAME = "Chorus"

/** `LyricCast-songs-1.png` and `LyricCast-songs-2.png`. */
class SongsScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SongList() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                MainScreenshot(data)
            }
        }
    }

    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SongEditor() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SongEditorScreen(
                    state = MutableSongEditorState().apply {
                        categories = data.categories
                        songTitle = data.awesomeSong.title
                        songCategory = data.awesomeSong.category
                        lyricsSections = listOf(VERSE_NAME, CHORUS_NAME)
                        lyricsSectionName = VERSE_NAME
                        lyricsSectionContent = PreviewData.sampleLyrics
                        currentSectionIndex = 0
                    }
                )
            }
        }
    }
}
