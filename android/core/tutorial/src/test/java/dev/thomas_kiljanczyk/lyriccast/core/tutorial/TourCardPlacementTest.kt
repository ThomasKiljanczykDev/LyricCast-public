/*
 * Created by Tomasz Kiljanczyk on 7/28/26, 6:42 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 7/28/26, 5:26 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TourCardPlacementTest {

    private val containerWidth = 1000f
    private val containerHeight = 2000f
    private val cardWidth = 400f
    private val cardHeight = 300f

    private fun place(
        anchor: Rect?,
        insetBottom: Float = 0f,
        insetTop: Float = 0f,
        position: TourCardPosition = TourCardPosition.AUTO
    ) = TourCardPlacement.offsetFor(
        anchor = anchor,
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        containerWidth = containerWidth,
        containerHeight = containerHeight,
        insetTop = insetTop,
        insetBottom = insetBottom,
        gap = 16f,
        position = position
    )

    @Test
    fun `anchor near the top places the card below it`() {
        val anchor = Rect(Offset(400f, 100f), Size(200f, 100f))

        val (_, y) = place(anchor)

        assertThat(y).isEqualTo(anchor.bottom + 16f)
    }

    @Test
    fun `anchor near the bottom places the card above it`() {
        val anchor = Rect(Offset(400f, 1850f), Size(200f, 100f))

        val (_, y) = place(anchor)

        assertThat(y).isEqualTo(anchor.top - 16f - cardHeight)
    }

    @Test
    fun `card is horizontally centred on the anchor`() {
        val anchor = Rect(Offset(400f, 100f), Size(200f, 100f))

        val (x, _) = place(anchor)

        assertThat(x).isEqualTo(anchor.center.x - cardWidth / 2f)
    }

    @Test
    fun `card is clamped into the container when the anchor sits at an edge`() {
        // A rail item hugging the left edge would otherwise push the card off-screen.
        val anchor = Rect(Offset(0f, 900f), Size(80f, 80f))

        val (x, _) = place(anchor)

        assertThat(x).isAtLeast(0f)
        assertThat(x + cardWidth).isAtMost(containerWidth)
    }

    @Test
    fun `card never lands under the bottom inset`() {
        val anchor = Rect(Offset(400f, 100f), Size(200f, 100f))

        val (_, y) = place(anchor, insetBottom = 1500f)

        assertThat(y + cardHeight).isAtMost(containerHeight - 1500f)
    }

    @Test
    fun `BOTTOM keeps the card clear of a menu dropping from the top bar`() {
        // AUTO would place the card just below the anchor, underneath the open menu.
        val overflowAnchor = Rect(Offset(900f, 40f), Size(80f, 80f))

        val (_, y) = place(overflowAnchor, position = TourCardPosition.BOTTOM)

        assertThat(y).isEqualTo(containerHeight - cardHeight)
        assertThat(y).isGreaterThan(overflowAnchor.bottom)
    }

    @Test
    fun `TOP keeps the card clear of the FAB menu expanding upwards`() {
        val fabAnchor = Rect(Offset(850f, 1850f), Size(100f, 100f))

        val (_, y) = place(fabAnchor, position = TourCardPosition.TOP)

        assertThat(y).isEqualTo(0f)
    }

    @Test
    fun `a forced position still respects insets`() {
        val anchor = Rect(Offset(900f, 40f), Size(80f, 80f))

        val (_, y) = place(anchor, insetBottom = 200f, position = TourCardPosition.BOTTOM)

        assertThat(y + cardHeight).isEqualTo(containerHeight - 200f)
    }

    @Test
    fun `a forced position still centres horizontally on the anchor`() {
        val anchor = Rect(Offset(300f, 40f), Size(80f, 80f))

        val (x, _) = place(anchor, position = TourCardPosition.BOTTOM)

        assertThat(x).isEqualTo(anchor.center.x - cardWidth / 2f)
    }

    @Test
    fun `anchorless steps are centred`() {
        val (x, y) = place(anchor = null)

        assertThat(x).isEqualTo((containerWidth - cardWidth) / 2f)
        assertThat(y).isEqualTo((containerHeight - cardHeight) / 2f)
    }
}
