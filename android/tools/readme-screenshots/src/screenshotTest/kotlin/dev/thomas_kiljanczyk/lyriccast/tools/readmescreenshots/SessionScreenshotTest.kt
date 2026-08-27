/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.PreviewData
import dev.thomas_kiljanczyk.lyriccast.core.ui.preview.rememberScreenshotData
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.model.GmsNearbySessionItem
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.choose_session.ChooseSessionDialog
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.choose_session.MutableChooseSessionDialogState
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SessionClientScreen
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SetlistInfo
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SetlistSongInfo
import kotlinx.collections.immutable.persistentListOf

/** The session a client can see, as the host named it. */
private const val SESSION_NAME = "My Session"

/**
 * `LyricCast-session-1.png` and `LyricCast-session-2.png` -- the two halves of joining a session:
 * picking one of the sessions in range, and following the host's lyrics once joined.
 */
class SessionScreenshotTest {
    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun ChooseSession() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                DialogScreenshot(
                    background = { MainScreenshot(data) },
                    dialog = {
                        ChooseSessionDialog(
                            state = MutableChooseSessionDialogState().apply {
                                devices = persistentListOf(
                                    GmsNearbySessionItem(SESSION_NAME, "endpoint1")
                                )
                                hasError = false
                                selectedEndpointId = null
                            },
                            modifier = DialogInsetModifier
                        )
                    }
                )
            }
        }
    }

    @PreviewTest
    @ReadmeScreenshot
    @Composable
    fun SessionClient() {
        val data = rememberScreenshotData()
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SessionClientScreen(
                    songTitle = data.awesomeSong.title,
                    slideText = PreviewData.sampleLyrics,
                    currentSlide = 1,
                    totalSlideCount = 3,
                    setlist = SetlistInfo(
                        songs = listOf(data.aSong, data.awesomeSong, data.greatSong)
                            .map { SetlistSongInfo(id = it.id.toString(), title = it.title) },
                        currentSongIndex = 1
                    ),
                    snackbarHostState = remember { SnackbarHostState() },
                    onNavigateUp = {}
                )
            }
        }
    }
}
