package dev.thomas_kiljanczyk.lyriccast.core.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

private const val SPOTLIGHT_PADDING_DP = 8
private const val SPOTLIGHT_CORNER_DP = 16
private const val CARD_GAP_DP = 16
private const val CARD_MAX_WIDTH_DP = 400

/**
 * Renders [content] with the guided-tour overlay on top.
 *
 * Wraps the whole `NavHost` rather than being a destination in it:
 * it has to draw over whichever screen the tour has navigated to.
 *
 * While a step is active it swallows pointer input and strips semantics from [content],
 * so both touch and TalkBack address the tour rather than the screen behind it.
 *
 * @param onAnchorMissing the step's anchor published no bounds,
 *   and did not opt into the anchorless fallback.
 *   The host is expected to advance the tour.
 * @param drawSpotlight false for anchors inside a popup,
 *   which draws above the scrim and so cannot be cut out.
 */
@Composable
fun TourHost(
    registry: TourAnchorRegistry,
    step: TourStep?,
    stepIndex: Int,
    stepCount: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onAnchorMissing: (TourStep) -> Unit,
    modifier: Modifier = Modifier,
    expansion: TourExpansion = TourExpansion.None,
    cardPosition: TourCardPosition = TourCardPosition.AUTO,
    drawSpotlight: Boolean = true,
    content: @Composable () -> Unit
) {
    val spotlitAnchor = step?.anchor
    val spotlight = remember(spotlitAnchor) { TourSpotlight { it == spotlitAnchor } }

    CompositionLocalProvider(
        LocalTourAnchorRegistry provides registry,
        LocalTourExpansion provides expansion,
        LocalTourSpotlight provides spotlight
    ) {
        Box(modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .then(
                        if (step != null) Modifier.clearAndSetSemantics { } else Modifier
                    )
            ) {
                content()
            }

            if (step != null) {
                TourOverlay(
                    step = step,
                    stepIndex = stepIndex,
                    stepCount = stepCount,
                    anchorBounds = step.anchor?.let { registry[it] },
                    onNext = onNext,
                    onSkip = onSkip,
                    onAnchorMissing = onAnchorMissing,
                    cardPosition = cardPosition,
                    drawSpotlight = drawSpotlight
                )
            }
        }
    }
}

@Composable
private fun TourOverlay(
    step: TourStep,
    stepIndex: Int,
    stepCount: Int,
    anchorBounds: Rect?,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onAnchorMissing: (TourStep) -> Unit,
    cardPosition: TourCardPosition,
    drawSpotlight: Boolean
) {
    val settlingAnchor = step.anchor != null && anchorBounds == null && !step.keepWhenAnchorMissing
    val currentOnAnchorMissing by rememberUpdatedState(onAnchorMissing)
    LaunchedEffect(step, anchorBounds) {
        if (settlingAnchor) {
            delay(ANCHOR_SETTLE_MILLIS.milliseconds)
            currentOnAnchorMissing(step)
        }
    }
    if (settlingAnchor) return

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(TOUR_ENTER_FADE_MILLIS))
    ) {
        TourOverlayContent(
            step = step,
            stepIndex = stepIndex,
            stepCount = stepCount,
            anchorBounds = anchorBounds,
            onNext = onNext,
            onSkip = onSkip,
            cardPosition = cardPosition,
            drawSpotlight = drawSpotlight
        )
    }
}

@Composable
private fun TourOverlayContent(
    step: TourStep,
    stepIndex: Int,
    stepCount: Int,
    anchorBounds: Rect?,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    cardPosition: TourCardPosition,
    drawSpotlight: Boolean
) {
    val registry = LocalTourAnchorRegistry.current
    // Keyed on presence rather than on the bounds themselves,
    // so the scroll it causes does not cancel and restart it.
    val anchorPresent = anchorBounds != null
    LaunchedEffect(step, anchorPresent) {
        if (anchorPresent) {
            step.anchor?.let { registry.bringIntoView(it) }
        }
    }

    val density = LocalDensity.current
    val scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.72f)
    val spotlightPadding = with(density) { SPOTLIGHT_PADDING_DP.dp.toPx() }
    val spotlightCorner = with(density) { SPOTLIGHT_CORNER_DP.dp.toPx() }
    val cardGap = with(density) { CARD_GAP_DP.dp.toPx() }

    // Anchor bounds are screen space (see TourAnchorRegistry), so rebase them on the overlay.
    var rootOffset by remember { mutableStateOf(Offset.Zero) }
    val localAnchor = anchorBounds?.translate(-rootOffset.x, -rootOffset.y)

    val safeInsets = WindowInsets.safeDrawing.asPaddingValues()
    val insetTop = with(density) { safeInsets.calculateTopPadding().toPx() }
    val insetBottom = with(density) { safeInsets.calculateBottomPadding().toPx() }

    Box(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootOffset = it.positionOnScreen() }
            .consumeAllPointerInput(key = step)
            .semantics { isTraversalGroup = true }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cutout = localAnchor.takeIf { drawSpotlight }
            if (cutout == null) {
                drawRect(scrimColor)
            } else {
                val path = Path().apply {
                    fillType = PathFillType.EvenOdd
                    addRect(Rect(Offset.Zero, size))
                    addRoundRect(
                        RoundRect(
                            rect = cutout.inflate(spotlightPadding),
                            cornerRadius = CornerRadius(spotlightCorner, spotlightCorner)
                        )
                    )
                }
                drawPath(path, scrimColor)
            }
        }

        val cardMaxWidth = with(density) { CARD_MAX_WIDTH_DP.dp.roundToPx() }
        Layout(
            content = {
                TourCard(
                    step = step,
                    stepIndex = stepIndex,
                    stepCount = stepCount,
                    onNext = onNext,
                    onSkip = onSkip
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { measurables, constraints ->
            val cardConstraints = Constraints(
                maxWidth = minOf(cardMaxWidth, constraints.maxWidth),
                maxHeight = constraints.maxHeight
            )
            val placeable = measurables.first().measure(cardConstraints)
            val (x, y) = TourCardPlacement.offsetFor(
                anchor = localAnchor,
                cardWidth = placeable.width.toFloat(),
                cardHeight = placeable.height.toFloat(),
                containerWidth = constraints.maxWidth.toFloat(),
                containerHeight = constraints.maxHeight.toFloat(),
                insetTop = insetTop,
                insetBottom = insetBottom,
                gap = cardGap,
                position = cardPosition
            )
            layout(constraints.maxWidth, constraints.maxHeight) {
                placeable.place(x.toInt(), y.toInt())
            }
        }
    }
}

@Composable
private fun TourCard(
    step: TourStep,
    stepIndex: Int,
    stepCount: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val isLast = stepIndex >= stepCount - 1
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = CARD_MAX_WIDTH_DP.dp)
            .semantics {
                // Steps replace each other in place, so focus never moves on its own.
                liveRegion = LiveRegionMode.Assertive
            }
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = stringResource(step.titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(step.bodyRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.tutorial_step_counter,
                        stepIndex + 1,
                        stepCount
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onSkip) {
                        Text(stringResource(R.string.tutorial_skip))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onNext) {
                        Text(
                            stringResource(
                                if (isLast) R.string.tutorial_done else R.string.tutorial_next
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Consumes every pointer event so it never reaches [content] behind the overlay.
 *
 * A pointer already consumed elsewhere (e.g. by a nested draggable) is left alone
 * until it's released, rather than being force-consumed on every subsequent move.
 */
private fun Modifier.consumeAllPointerInput(key: Any?): Modifier = pointerInput(key) {
    val claimedElsewhere = mutableSetOf<Long>()
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)
            event.changes.forEach {
                if (!it.pressed) {
                    claimedElsewhere.remove(it.id.value)
                }
                if (it.isConsumed) {
                    claimedElsewhere.add(it.id.value)
                }
                if (it.id.value !in claimedElsewhere) {
                    it.consume()
                }
            }
        }
    }
}

private const val ANCHOR_SETTLE_MILLIS = 350L

private const val TOUR_ENTER_FADE_MILLIS = 300
