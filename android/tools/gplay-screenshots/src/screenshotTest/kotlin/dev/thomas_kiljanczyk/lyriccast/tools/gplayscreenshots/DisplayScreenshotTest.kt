/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 AM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.gplayscreenshots

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackState
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_controls.SetlistControlsScreen

/** The lyrics-display / control screen shown during a service -- the app's signature screen. */
class DisplayScreenshotTest {
    @PreviewTest
    @StoreScreenshots
    @Composable
    fun Display() {
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SetlistControlsScreen(
                    state = PlaybackState(
                        songs = PreviewData.sampleSongsWithLyrics,
                        currentSlideText = "Amazing grace, how sweet the sound\n" +
                            "That saved a wretch like me",
                        songTitle = "Amazing Grace",
                        currentSlide = 1,
                        totalSlideCount = 4,
                        currentSongPosition = 1,
                        isBlanked = false
                    ),
                    onNavigateUp = {},
                    onNavigateToSettings = {},
                    onPreviousClick = {},
                    onNextClick = {},
                    onBlankClick = {},
                    onSongClick = {}
                )
            }
        }
    }
}
