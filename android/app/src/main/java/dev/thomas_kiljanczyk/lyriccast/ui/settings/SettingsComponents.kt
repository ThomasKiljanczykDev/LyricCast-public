/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 5:21 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 5:20 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.settings

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun SettingsCategory(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun SettingsCardGroup(
    modifier: Modifier = Modifier,
    content: @Composable SettingsCardGroupScope.() -> Unit
) {
    val scope = SettingsCardGroupScopeImpl()
    scope.content()

    if (scope.items.isEmpty()) {
        return
    }

    Column(
        modifier = modifier
    ) {
        scope.items.forEachIndexed { index, item ->
            val isFirst = index == 0
            val isLast = index == scope.items.lastIndex

            if (!isFirst) {
                Spacer(modifier = Modifier.height(2.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = when {
                    isFirst && isLast -> RoundedCornerShape(16.dp)

                    isFirst -> RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )

                    isLast -> RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )

                    else -> RoundedCornerShape(4.dp)
                }
            ) {
                item()
            }
        }
    }
}

interface SettingsCardGroupScope {
    fun item(content: @Composable () -> Unit)
}

private class SettingsCardGroupScopeImpl : SettingsCardGroupScope {
    val items = mutableListOf<@Composable () -> Unit>()

    override fun item(content: @Composable () -> Unit) {
        items.add(content)
    }
}

@Composable
fun <T> SettingsRowWithDialog(
    title: String,
    value: T,
    options: ImmutableList<Pair<T, UiText>>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(
                onClick = { showDialog = true },
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = options.find { it.first == value }?.second?.asString() ?: value.toString(),
            style = MaterialTheme.typography.bodySmall,
        )
    }

    if (showDialog) {
        SettingsDialog(
            title = title,
            options = options,
            selectedValue = value,
            onOptionSelected = { onValueChange(it) },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun SettingsCheckbox(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onCheckedChange(!checked) },
                interactionSource = interactionSource,
                indication = ripple(true)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier
                .combinedClickable(
                    onClick = { onCheckedChange(!checked) },
                    interactionSource = interactionSource,
                    indication = ripple(false)
                )
                .padding(4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                interactionSource = interactionSource,
                modifier = Modifier.weight(1f),
                thumb = {
                    SliderDefaults.Thumb(
                        modifier = Modifier.height(32.dp),
                        interactionSource = interactionSource,
                    )
                }
            )
            Text(
                text = value.toInt().toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsCategory() {
    LyricCastTheme {
        Surface {
            SettingsCategory(title = "General", content = {
                SettingsCardGroup(content = {
                    item {
                        SettingsRowWithDialog(
                            title = "Theme",
                            value = 1,
                            options = listOf(
                                1 to UiText.DynamicString("Light"),
                                2 to UiText.DynamicString("Dark")
                            ).toImmutableList(),
                            onValueChange = { }
                        )
                    }
                    item {
                        SettingsCheckbox(
                            title = "Enable Feature",
                            checked = true,
                            onCheckedChange = { }
                        )
                    }
                })
            })
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsCardGroup() {
    LyricCastTheme {
        Surface {
            SettingsCardGroup {
                item {
                    SettingsRowWithDialog(
                        title = "Theme",
                        value = 1,
                        options = listOf(
                            1 to UiText.DynamicString("System default"),
                            2 to UiText.DynamicString("Light"),
                            3 to UiText.DynamicString("Dark")
                        ).toImmutableList(),
                        onValueChange = { }
                    )
                }
                item {
                    SettingsRowWithDialog(
                        title = "Button Height",
                        value = 2,
                        options = listOf(
                            1 to UiText.DynamicString("Small"),
                            2 to UiText.DynamicString("Medium"),
                            3 to UiText.DynamicString("Large")
                        ).toImmutableList(),
                        onValueChange = { }
                    )
                }
                item {
                    SettingsCheckbox(
                        title = "Enable Blank Screen",
                        checked = true,
                        onCheckedChange = { }
                    )
                }
                item {
                    SettingsSlider(
                        title = "Font Size",
                        value = 75f,
                        valueRange = 30f..100f,
                        onValueChange = { }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsRowWithDialog() {
    LyricCastTheme {
        Surface {
            SettingsRowWithDialog(
                title = "Choose Option",
                value = 1,
                options = listOf(
                    1 to UiText.DynamicString("Option 1"),
                    2 to UiText.DynamicString("Option 2"),
                    3 to UiText.DynamicString("Option 3")
                ).toImmutableList(),
                onValueChange = { }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsCheckbox() {
    LyricCastTheme {
        Surface {
            SettingsCheckbox(
                title = "Enable Feature",
                checked = true,
                onCheckedChange = { }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettingsSlider() {
    LyricCastTheme {
        Surface {
            SettingsSlider(
                title = "Volume",
                value = 6f,
                valueRange = 0f..10f,
                onValueChange = { }
            )
        }
    }
}
