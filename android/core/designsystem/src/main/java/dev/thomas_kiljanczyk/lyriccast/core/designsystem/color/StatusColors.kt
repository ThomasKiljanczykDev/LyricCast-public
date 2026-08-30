/*
 * Created by Tomasz Kiljanczyk on 8/31/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/31/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.designsystem.color

import androidx.compose.ui.graphics.Color

/**
 * On/off pair for the blank control. Outside the Material scheme on purpose: they carry meaning,
 * not brand, so they must not shift with dynamic colour or day/night.
 */
@Suppress("MagicNumber") // The literals are the colours themselves.
data object StatusColors {
    val On = Color(0xFF22AA22)
    val Off = Color(0xFFCC4444)
}
