/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:59 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme

@Composable
internal fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    val dotSize = 8.dp
    val gap = 8.dp
    val pillWidth = 24.dp

    // Start-aligned,
    // so every dot keeps its left edge and the group never slides as a whole.
    Box(
        modifier = modifier
            .width(dotSize * pageCount + gap * (pageCount - 1) + (pillWidth - dotSize))
            // Decorative:
            // the pager already exposes the page count to accessibility services.
            .clearAndSetSemantics { },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { index ->
                val selected = index == currentPage
                val width by animateDpAsState(
                    targetValue = if (selected) pillWidth else dotSize,
                    animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    label = "indicatorWidth"
                )
                val color by animateColorAsState(
                    targetValue = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    label = "indicatorColor"
                )
                Box(
                    Modifier
                        .height(dotSize)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

/**
 * Tap to advance:
 * the transfer only animates in interactive preview mode.
 */
@PreviewLightDark
@Composable
private fun PageIndicatorPreview() {
    LyricCastTheme {
        Surface {
            var page by remember { mutableIntStateOf(0) }
            Column(
                modifier = Modifier
                    .clickable { page = (page + 1) % 6 }
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PageIndicator(pageCount = 6, currentPage = page)
                PageIndicator(pageCount = 6, currentPage = 0)
                PageIndicator(pageCount = 6, currentPage = 3)
                PageIndicator(pageCount = 6, currentPage = 5)
            }
        }
    }
}
