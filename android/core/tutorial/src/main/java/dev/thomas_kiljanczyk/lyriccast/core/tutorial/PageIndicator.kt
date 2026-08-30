/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/30/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme

private val DotSize = 8.dp
private val DotSpacing = 8.dp
private const val MIN_CAPSULE_WEIGHT = 0.01f

/**
 * Dots for [pageCount] pages.
 * The selected dot is a capsule that stretches to the next dot, then contracts behind it.
 *
 * @param progress `pagerState.currentPage + pagerState.currentPageOffsetFraction`,
 *   as a lambda so a swipe redraws without recomposing.
 */
@Composable
internal fun PageIndicator(pageCount: Int, progress: () -> Float, modifier: Modifier = Modifier) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.surfaceVariant
    val size = DpSize(
        width = DotSize * pageCount + DotSpacing * (pageCount - 1),
        height = DotSize
    )

    Canvas(
        modifier = modifier
            .size(size)
            // Decorative: the pager already announces the page count.
            .clearAndSetSemantics { }
    ) {
        val dotSizePx = DotSize.toPx()
        val radius = dotSizePx / 2f
        val step = dotSizePx + DotSpacing.toPx()
        val centerY = this.size.height / 2f

        repeat(pageCount) { index ->
            drawCircle(
                color = unselectedColor,
                radius = radius,
                center = Offset(radius + step * index, centerY)
            )
        }

        val position = progress().coerceIn(0f, (pageCount - 1).toFloat())
        val selectedPage = position.toInt()
        val offset = position - selectedPage

        val startWeight = (1f - offset * 2f).coerceAtLeast(0f)
        val endWeight = (offset * 2f - 1f).coerceAtLeast(0f)
        // Zero-length round caps render unreliably.
        val capsuleWeight = (1f - startWeight - endWeight).coerceAtLeast(MIN_CAPSULE_WEIGHT)

        val capsuleStart = radius + step * (selectedPage + endWeight)
        drawLine(
            color = selectedColor,
            start = Offset(capsuleStart, centerY),
            end = Offset(capsuleStart + step * capsuleWeight, centerY),
            strokeWidth = dotSizePx,
            cap = StrokeCap.Round
        )
    }
}

/** Tap to advance; animates in interactive preview only. */
@Suppress("MagicNumber")
@PreviewLightDark
@Composable
private fun PageIndicatorPreview() {
    LyricCastTheme {
        Surface {
            var page by remember { mutableFloatStateOf(0f) }
            Column(
                modifier = Modifier
                    .clickable { page = (page + 1f) % 6f }
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PageIndicator(pageCount = 6, progress = { page })
                PageIndicator(pageCount = 6, progress = { 0f })
                PageIndicator(pageCount = 6, progress = { 2.25f })
                PageIndicator(pageCount = 6, progress = { 2.5f })
                PageIndicator(pageCount = 6, progress = { 2.75f })
                PageIndicator(pageCount = 6, progress = { 5f })
            }
        }
    }
}
