package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.ui.geometry.Rect
import kotlin.math.max
import kotlin.math.min

/**
 * Where the tooltip card sits when the automatic rule is not good enough.
 *
 * Menus render in their own platform window, which draws *above* the tour overlay,
 * so a card placed next to the control that opened a menu ends up underneath the menu.
 * Steps that force a menu open pin the card to the opposite edge instead.
 */
enum class TourCardPosition {
    /** Beside the anchor: below it when there is room, otherwise above. */
    AUTO,

    /** Top of the content area — for menus that expand upwards, like the FAB. */
    TOP,

    /** Bottom of the content area — for menus that drop down from the top bar. */
    BOTTOM
}

/** Pure numbers so the placement rules stay testable without a Compose test rule. */
object TourCardPlacement {

    /**
     * Prefers below the anchor,
     * falls back to above,
     * and centres horizontally on it.
     *
     * When it fits neither side the card is clamped into the content area,
     * overlapping the anchor:
     * showing the text matters more than preserving the cutout.
     */
    fun offsetFor(
        anchor: Rect?,
        cardWidth: Float,
        cardHeight: Float,
        containerWidth: Float,
        containerHeight: Float,
        insetLeft: Float = 0f,
        insetTop: Float = 0f,
        insetRight: Float = 0f,
        insetBottom: Float = 0f,
        gap: Float = 0f,
        position: TourCardPosition = TourCardPosition.AUTO
    ): Pair<Float, Float> {
        val minX = insetLeft
        val maxX = max(minX, containerWidth - insetRight - cardWidth)
        val minY = insetTop
        val maxY = max(minY, containerHeight - insetBottom - cardHeight)

        // A forced edge still centres on the anchor,
        // so the card stays visually associated with what it describes.
        if (position != TourCardPosition.AUTO) {
            val x = if (anchor == null) {
                clamp((containerWidth - cardWidth) / 2f, minX, maxX)
            } else {
                clamp(anchor.center.x - cardWidth / 2f, minX, maxX)
            }
            val y = if (position == TourCardPosition.TOP) minY else maxY
            return x to y
        }

        if (anchor == null) {
            val x = clamp((containerWidth - cardWidth) / 2f, minX, maxX)
            val y = clamp((containerHeight - cardHeight) / 2f, minY, maxY)
            return x to y
        }

        val below = anchor.bottom + gap
        val above = anchor.top - gap - cardHeight
        val fitsBelow = below + cardHeight <= containerHeight - insetBottom
        val fitsAbove = above >= insetTop

        val y = when {
            fitsBelow -> below
            fitsAbove -> above
            else -> clamp((containerHeight - cardHeight) / 2f, minY, maxY)
        }

        val x = clamp(anchor.center.x - cardWidth / 2f, minX, maxX)
        return x to clamp(y, minY, maxY)
    }

    private fun clamp(value: Float, minValue: Float, maxValue: Float): Float =
        min(max(value, minValue), maxValue)
}
