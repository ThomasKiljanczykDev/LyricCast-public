package dev.thomas_kiljanczyk.lyriccast.core.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

/**
 * Reports [min] as this element's minimum intrinsic width, while measuring and rendering its content
 * unchanged.
 *
 * `FlowRow` decides where a weighted child wraps from that child's *minIntrinsicWidth* (see
 * `Measurable.measureAndCache` in foundation's `FlowLayout`), then distributes the leftover space on
 * the line by weight. A Material `TextField` imposes a `defaultMinSize(minWidth = 280.dp)`, which
 * floors its min-intrinsic width — so two text fields only share a row once the width can give each
 * 280 dp, and `Modifier.widthIn(min = …)` can only push that floor higher, never lower.
 *
 * This modifier lowers the value FlowRow sees for line-breaking back to a chosen [min] (e.g. 220 dp)
 * without changing how the field actually measures: when the field ends up on a tight shared row,
 * `defaultMinSize` simply yields to the tighter incoming constraint, so the field renders at the
 * narrower width with no clipping. Use it on the weighted fields inside a filter
 * [androidx.compose.foundation.layout.FlowRow] to control the width at which they wrap to share
 * a row.
 */
fun Modifier.flowItemMinWidth(min: Dp): Modifier = this then FlowItemMinWidthElement(min)

private data class FlowItemMinWidthElement(val min: Dp) :
    ModifierNodeElement<FlowItemMinWidthNode>() {
    override fun create() = FlowItemMinWidthNode(min)

    override fun update(node: FlowItemMinWidthNode) {
        node.min = min
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "flowItemMinWidth"
        value = min
    }
}

private class FlowItemMinWidthNode(var min: Dp) : Modifier.Node(), LayoutModifierNode {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int = min.roundToPx()
}
