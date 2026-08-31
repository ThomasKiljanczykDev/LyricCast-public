package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme

private const val MAX_SWIPE_OFFSET_DP = 200

enum class SwipeDirection {
    LEFT,
    RIGHT,
    BOTH
}

@Stable
class SwipeToRevealState {
    var targetOffsetX by mutableStateOf(0.dp)
    var currentOffsetX by mutableStateOf(0.dp)
    var hasTriggeredLeftThreshold by mutableStateOf(false)
    var hasTriggeredRightThreshold by mutableStateOf(false)

    fun onDragStart() {
        hasTriggeredLeftThreshold = false
        hasTriggeredRightThreshold = false
    }

    fun onDragEnd(
        leftThreshold: Dp? = null,
        rightThreshold: Dp? = null,
        onSwipeLeft: (() -> Unit)? = null,
        onSwipeRight: (() -> Unit)? = null
    ) {
        when {
            leftThreshold != null && currentOffsetX <= leftThreshold -> {
                onSwipeLeft?.invoke()
                reset()
            }

            rightThreshold != null && currentOffsetX >= rightThreshold -> {
                onSwipeRight?.invoke()
                reset()
            }

            else -> {
                animateToReset()
            }
        }
        hasTriggeredLeftThreshold = false
        hasTriggeredRightThreshold = false
    }

    fun updateOffset(
        newOffset: Dp,
        minOffset: Dp = -MAX_SWIPE_OFFSET_DP.dp,
        maxOffset: Dp = MAX_SWIPE_OFFSET_DP.dp
    ) {
        currentOffsetX = newOffset.coerceIn(minOffset, maxOffset)
        targetOffsetX = currentOffsetX
    }

    fun checkLeftThresholdHaptic(threshold: Dp): Boolean {
        val shouldTrigger = currentOffsetX <= threshold && !hasTriggeredLeftThreshold
        if (shouldTrigger) {
            hasTriggeredLeftThreshold = true
        } else if (currentOffsetX > threshold) {
            hasTriggeredLeftThreshold = false
        }
        return shouldTrigger
    }

    fun checkRightThresholdHaptic(threshold: Dp): Boolean {
        val shouldTrigger = currentOffsetX >= threshold && !hasTriggeredRightThreshold
        if (shouldTrigger) {
            hasTriggeredRightThreshold = true
        } else if (currentOffsetX < threshold) {
            hasTriggeredRightThreshold = false
        }
        return shouldTrigger
    }

    fun reset() {
        targetOffsetX = 0.dp
        currentOffsetX = 0.dp
        hasTriggeredLeftThreshold = false
        hasTriggeredRightThreshold = false
    }

    fun animateToReset() {
        targetOffsetX = 0.dp
        currentOffsetX = 0.dp
    }
}

@Composable
fun rememberSwipeToRevealState(): SwipeToRevealState {
    return remember { SwipeToRevealState() }
}

@Composable
private fun SwipeBackground(
    icon: ImageVector,
    hasTriggeredThreshold: Boolean,
    baseBackgroundColor: Color,
    triggeredBackgroundColor: Color,
    baseIconColor: Color,
    triggeredIconColor: Color,
    arrangement: Arrangement.Horizontal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (hasTriggeredThreshold) triggeredBackgroundColor else baseBackgroundColor,
        label = "swipe_bg_color"
    )

    val iconColor by animateColorAsState(
        targetValue = if (hasTriggeredThreshold) triggeredIconColor else baseIconColor,
        label = "swipe_icon_color"
    )

    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = arrangement,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Swipe action",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SwipeToRevealBox(
    modifier: Modifier = Modifier,
    state: SwipeToRevealState = rememberSwipeToRevealState(),
    swipeDirection: SwipeDirection = SwipeDirection.BOTH,
    leftSwipeThreshold: Dp = (-150).dp,
    rightSwipeThreshold: Dp = 150.dp,
    maxSwipeDistance: Dp = 200.dp,
    enableSwipe: Boolean = true,
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeRight: (() -> Unit)? = null,
    leftIcon: ImageVector = Icons.Rounded.Delete,
    rightIcon: ImageVector = Icons.Rounded.Archive,
    content: @Composable BoxScope.() -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    val animatedOffsetX by animateDpAsState(
        targetValue = state.targetOffsetX,
        label = "swipe_offset"
    )

    val displayOffsetX = if (state.targetOffsetX == state.currentOffsetX)
        animatedOffsetX
    else
        state.currentOffsetX

    Box(modifier = modifier.fillMaxWidth()) {
        if (displayOffsetX < 0.dp && (swipeDirection == SwipeDirection.LEFT || swipeDirection == SwipeDirection.BOTH)) {
            SwipeBackground(
                modifier = Modifier.matchParentSize(),
                icon = leftIcon,
                hasTriggeredThreshold = state.hasTriggeredLeftThreshold,
                baseBackgroundColor = MaterialTheme.colorScheme.errorContainer,
                triggeredBackgroundColor = MaterialTheme.colorScheme.error,
                baseIconColor = MaterialTheme.colorScheme.onErrorContainer,
                triggeredIconColor = MaterialTheme.colorScheme.onError,
                arrangement = Arrangement.End,
                onClick = { onSwipeLeft?.invoke() }
            )
        }

        if (displayOffsetX > 0.dp &&
            (swipeDirection == SwipeDirection.RIGHT || swipeDirection == SwipeDirection.BOTH)
        ) {
            SwipeBackground(
                modifier = Modifier.matchParentSize(),
                icon = rightIcon,
                hasTriggeredThreshold = state.hasTriggeredRightThreshold,
                baseBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                triggeredBackgroundColor = MaterialTheme.colorScheme.tertiary,
                baseIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                triggeredIconColor = MaterialTheme.colorScheme.onTertiary,
                arrangement = Arrangement.Start,
                onClick = { onSwipeRight?.invoke() }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = displayOffsetX)
                .pointerInput(enableSwipe, swipeDirection) {
                    if (enableSwipe) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                state.onDragStart()
                            },
                            onDragEnd = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                state.onDragEnd(
                                    leftThreshold = if (swipeDirection != SwipeDirection.RIGHT) {
                                        leftSwipeThreshold
                                    } else {
                                        null
                                    },
                                    rightThreshold = if (swipeDirection != SwipeDirection.LEFT) {
                                        rightSwipeThreshold
                                    } else {
                                        null
                                    },
                                    onSwipeLeft = onSwipeLeft,
                                    onSwipeRight = onSwipeRight
                                )
                            }
                        ) { _, dragAmount ->
                            val newOffset = state.currentOffsetX + (dragAmount / 1.dp.toPx()).dp

                            val constrainedOffset = when (swipeDirection) {
                                SwipeDirection.LEFT -> newOffset.coerceAtMost(0.dp)
                                SwipeDirection.RIGHT -> newOffset.coerceAtLeast(0.dp)
                                SwipeDirection.BOTH -> newOffset
                            }

                            state.updateOffset(
                                constrainedOffset,
                                minOffset = -maxSwipeDistance,
                                maxOffset = maxSwipeDistance
                            )

                            if (swipeDirection != SwipeDirection.RIGHT &&
                                state.checkLeftThresholdHaptic(leftSwipeThreshold)
                            ) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            }
                            if (swipeDirection != SwipeDirection.LEFT &&
                                state.checkRightThresholdHaptic(rightSwipeThreshold)
                            ) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            }
                        }
                    }
                }
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun SwipeToRevealBoxPreview_LeftSwipeOnly() {
    LyricCastTheme {
        Surface {
            SwipeToRevealBox(
                swipeDirection = SwipeDirection.LEFT,
                leftSwipeThreshold = (-100).dp,
                onSwipeLeft = { /* Delete action */ },
                leftIcon = Icons.Rounded.Delete
            ) {
                Card {
                    ListItem(
                        headlineContent = { Text("Swipe left to delete") },
                        supportingContent = { Text("Try swiping this item to the left") }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SwipeToRevealBoxPreview_BidirectionalSwipe() {
    LyricCastTheme {
        Surface {
            SwipeToRevealBox(
                swipeDirection = SwipeDirection.BOTH,
                leftSwipeThreshold = (-100).dp,
                rightSwipeThreshold = 100.dp,
                onSwipeLeft = { /* Delete action */ },
                onSwipeRight = { /* Archive action */ },
                leftIcon = Icons.Rounded.Delete,
                rightIcon = Icons.Rounded.Favorite
            ) {
                Card {
                    ListItem(
                        headlineContent = { Text("Bidirectional swipe") },
                        supportingContent = { Text("Swipe left to delete, right to favorite") }
                    )
                }
            }
        }
    }
}
