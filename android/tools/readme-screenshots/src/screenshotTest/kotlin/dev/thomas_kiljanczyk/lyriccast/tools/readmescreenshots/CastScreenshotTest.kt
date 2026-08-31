package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackState
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.rememberScreenshotData
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_controls.SetlistControlsScreen
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_controls.SongControlsScreen

/**
 * `LyricCast-cast-1.png` and `LyricCast-cast-2.png` -- the two shapes the controls view takes:
 * a single song, and a setlist with its queue underneath.
 */
class CastScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SongControls() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SongControlsScreen(
                    state = PlaybackState(
                        songTitle = data.awesomeSong.title,
                        currentSlideText = PreviewData.sampleLyrics,
                        // Mid-song, so neither control button renders disabled.
                        currentSlide = 1,
                        totalSlideCount = 3,
                        isBlanked = false
                    ),
                    onNavigateUp = {},
                    onNavigateToSettings = {},
                    onPreviousClick = {},
                    onNextClick = {},
                    onBlankClick = {}
                )
            }
        }
    }

    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SetlistControls() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SetlistControlsScreen(
                    state = PlaybackState(
                        songTitle = data.awesomeSong.title,
                        currentSlideText = PreviewData.sampleLyrics,
                        currentSlide = 1,
                        totalSlideCount = 3,
                        isBlanked = false,
                        // The second song is the one being presented, so the queue shows a
                        // highlighted current item.
                        songs = listOf(
                            data.aSong,
                            data.awesomeSong.copy(isSelected = true),
                            data.greatSong
                        ),
                        currentSongPosition = 1
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
