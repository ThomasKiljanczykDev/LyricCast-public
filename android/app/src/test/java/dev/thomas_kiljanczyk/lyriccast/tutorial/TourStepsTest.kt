/*
 * Created by Tomasz Kiljanczyk on 8/31/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/31/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.tutorial

import com.google.common.truth.Truth.assertThat
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourCardPosition
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourExpandable
import org.junit.Test

/**
 * The overlay's behaviour is derived from the anchor rather than stored on the step,
 * so these three mappings are the only place the two can drift.
 */
class TourStepsTest {

    private val overflowItemAnchors = listOf(
        TourAnchor.MAIN_MENU_IMPORT,
        TourAnchor.MAIN_MENU_EXPORT,
        TourAnchor.MAIN_MENU_CATEGORIES,
        TourAnchor.MAIN_MENU_SOFT_DELETE,
        TourAnchor.MAIN_MENU_SETTINGS
    )

    private val overflowAnchors = overflowItemAnchors + TourAnchor.MAIN_OVERFLOW

    private val fabAnchors = listOf(
        TourAnchor.MAIN_FAB,
        TourAnchor.MAIN_FAB_MENU,
        TourAnchor.MAIN_FAB_ADD_SONG,
        TourAnchor.MAIN_FAB_ADD_SETLIST
    )

    @Test
    fun `overflow anchors force the overflow menu open`() {
        overflowAnchors.forEach { anchor ->
            assertThat(expandableFor(anchor)).isEqualTo(TourExpandable.MAIN_OVERFLOW_MENU)
        }
    }

    @Test
    fun `fab anchors force the fab menu open`() {
        fabAnchors.forEach { anchor ->
            assertThat(expandableFor(anchor)).isEqualTo(TourExpandable.MAIN_FAB_MENU)
        }
    }

    @Test
    fun `anchors outside a menu force nothing open`() {
        val standalone = TourAnchor.entries - overflowAnchors.toSet() - fabAnchors.toSet()

        standalone.forEach { anchor -> assertThat(expandableFor(anchor)).isNull() }
        assertThat(expandableFor(null)).isNull()
    }

    @Test
    fun `card is pinned to the edge the menu grows away from`() {
        assertThat(cardPositionFor(TourExpandable.MAIN_OVERFLOW_MENU))
            .isEqualTo(TourCardPosition.BOTTOM)
        assertThat(cardPositionFor(TourExpandable.MAIN_FAB_MENU)).isEqualTo(TourCardPosition.TOP)
        assertThat(cardPositionFor(null)).isEqualTo(TourCardPosition.AUTO)
    }

    @Test
    fun `overflow items highlight themselves instead of cutting out the scrim`() {
        overflowItemAnchors.forEach { anchor ->
            assertThat(drawsSpotlightFor(anchor)).isFalse()
        }
    }

    @Test
    fun `inline anchors cut out the scrim`() {
        // The overflow trigger is inline content; only the items in its popup are not.
        val inline = TourAnchor.entries - overflowItemAnchors.toSet()

        inline.forEach { anchor -> assertThat(drawsSpotlightFor(anchor)).isTrue() }
        assertThat(drawsSpotlightFor(null)).isTrue()
    }
}
