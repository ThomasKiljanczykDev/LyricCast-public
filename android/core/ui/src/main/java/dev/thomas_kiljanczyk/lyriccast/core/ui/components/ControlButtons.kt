/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 7:23 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 7:18 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.color.StatusColors
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.ui.R
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var isAnimating by remember { mutableStateOf(false) }
    val normalRadius = (ControlButtonHeightOption.DEFAULT.value / 2).dp
    val pressedRadius = (ControlButtonHeightOption.DEFAULT.value / 4).dp

    val animatedCornerRadius by animateDpAsState(
        targetValue = when {
            isPressed -> pressedRadius
            isAnimating -> pressedRadius
            else -> normalRadius
        },
        finishedListener = { isAnimating = false },
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "buttonCornerRadius"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            isAnimating = true
        } else if (isAnimating) {
            kotlinx.coroutines.delay(100.milliseconds)
            isAnimating = false
        }
    }

    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(animatedCornerRadius)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AnimatedButton(
    onClick: () -> Unit,
    isOn: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onColor: Color,
    offColor: Color,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val roundRadius = (ControlButtonHeightOption.DEFAULT.value / 2).dp
    val squareRadius = (ControlButtonHeightOption.DEFAULT.value / 4).dp

    // Reverse the radius logic based on isOn state
    val targetRadius = when {
        isPressed -> if (isOn) roundRadius else squareRadius
        else -> if (isOn) squareRadius else roundRadius
    }

    val animatedCornerRadius by animateDpAsState(
        targetValue = targetRadius,
        label = "buttonCornerRadius",
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val animatedButtonColor by animateColorAsState(
        targetValue = if (isOn) onColor else offColor,
        label = "buttonColor",
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedButtonColor
        ),
        shape = RoundedCornerShape(animatedCornerRadius)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ControlButtons(
    buttonHeight: Int,
    isBlanked: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBlankClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPreviousEnabled: Boolean = true,
    isNextEnabled: Boolean = true,
    /** Blanking only exists over Cast; the control is inert without a session. */
    isBlankEnabled: Boolean = true
) {
    Card(
        modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val buttonSize = (buttonHeight - 16).dp // Account for padding

            AnimatedFilledIconButton(
                onClick = onPreviousClick,
                enabled = isPreviousEnabled,
                modifier = Modifier.size(
                    height = buttonSize,
                    width = ControlButtonHeightOption.DEFAULT.value.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(R.string.controls_previous),
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(180f)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            AnimatedButton(
                onClick = onBlankClick,
                enabled = isBlankEnabled,
                isOn = !isBlanked, // Reverse logic: ON when not blanked
                onColor = StatusColors.On,
                offColor = StatusColors.Off,
                modifier = Modifier
                    .size(height = buttonSize, width = ControlButtonHeightOption.DEFAULT.value.dp)
            ) {
                Text(
                    text = stringResource(
                        if (isBlanked) R.string.controls_off else R.string.controls_on
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            AnimatedFilledIconButton(
                onClick = onNextClick,
                enabled = isNextEnabled,
                modifier = Modifier.size(
                    height = buttonSize,
                    width = ControlButtonHeightOption.DEFAULT.value.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(R.string.controls_next),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewControlButtons() {
    var isBlanked by remember { mutableStateOf(false) }

    LyricCastTheme {
        Surface {
            ControlButtons(
                buttonHeight = ControlButtonHeightOption.LARGE.value,
                isBlanked = isBlanked,
                onPreviousClick = {},
                onNextClick = {},
                onBlankClick = { isBlanked = !isBlanked }
            )
        }
    }
}
