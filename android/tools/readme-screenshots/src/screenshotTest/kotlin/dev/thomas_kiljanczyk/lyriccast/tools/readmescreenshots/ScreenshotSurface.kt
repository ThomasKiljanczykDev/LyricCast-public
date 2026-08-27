/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tools.readmescreenshots

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * On a real phone, Scaffold reserves space for the gesture-navigation inset at the bottom of the
 * screen. Layoutlib never dispatches real window insets, so that inset resolves to zero and a
 * full-bleed screen renders flush against the canvas edge -- its bottom corners look clipped
 * instead of rounded. This fakes that inset so the rendered screenshots match the device.
 */
private val SIMULATED_GESTURE_NAV_INSET = 24.dp

@Composable
fun ScreenshotSurface(content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = SIMULATED_GESTURE_NAV_INSET)
        ) {
            content()
        }
    }
}
