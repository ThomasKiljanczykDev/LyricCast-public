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
import dev.thomas_kiljanczyk.lyriccast.feature.session.impl.ui.session_client.SessionClientScreen

class SessionScreenshotTest {
    @PreviewTest
    @StoreScreenshots
    @Composable
    fun SessionClient() {
        LyricCastTheme(useDarkTheme = true) {
            ScreenshotSurface {
                SessionClientScreen(
                    songTitle = "Amazing Grace",
                    slideText = "Amazing grace, how sweet the sound\nThat saved a wretch like me\n" +
                        "I once was lost, but now am found\nWas blind, but now I see",
                    currentSlide = 0,
                    totalSlideCount = 4,
                    setlist = null,
                    snackbarHostState = remember { SnackbarHostState() },
                    onNavigateUp = {}
                )
            }
        }
    }
}
