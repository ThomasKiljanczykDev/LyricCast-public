
package dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.category_manager.ValidateCategoryNameUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.ColorItem
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastSpinner
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastTextField
import dev.thomas_kiljanczyk.lyriccast.core.ui.testing.TestTags
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.R
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddOrEditCategoryForm(
    state: AddOrEditCategoryState,
    onColorChange: (ColorItem) -> Unit = {},
    onNameChange: (String) -> Unit = {}
) {
    val colorItems = remember { colorItems }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        LyricCastTextField(
            label = stringResource(R.string.category_manager_hint_category_name),
            value = state.name,
            onValueChange = { onNameChange(it) },
            maxLength = ValidateCategoryNameUseCase.MAX_LENGTH,
            errorText = state.nameError?.asString(),
            singleLine = true,
            modifier = Modifier.testTag(TestTags.CATEGORY_NAME_FIELD)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val animatedColor by animateColorAsState(
                targetValue = Color(state.color.value),
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = animatedColor
                ), modifier = Modifier
                    .padding(start = 8.dp)
                    .height(30.dp)
                    .width(30.dp)
            ) {}
            LyricCastSpinner(
                options = colorItems,
                value = state.color.name.asString(),
                label = stringResource(R.string.category_manager_hint_category_color),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.CATEGORY_COLOR_DROPDOWN),
                onOptionSelected = {
                    onColorChange(it)
                }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(it.value)
                        ), modifier = Modifier
                            .height(30.dp)
                            .width(30.dp)
                    ) {}
                    Text(text = it.name.asString())
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewAddOrEditCategoryForm() {
    LyricCastTheme {
        Surface(modifier = Modifier.height(500.dp)) {
            AddOrEditCategoryForm(
                state = MutableAddOrEditCategoryState().apply {
                    name = "Sample Category"
                    color = ColorItem(
                        name = UiText.DynamicString("Red"), value = Color.Red.toArgb()
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun AddOrEditCategoryDialog(
    category: Category?, onDismiss: () -> Unit = {}
) {
    val dialogKey = remember { Uuid.random().toString() }
    val viewModel: AddOrEditCategoryDialogViewModel = hiltViewModel(key = dialogKey)

    val scope = rememberCoroutineScope()

    LaunchedEffect(category) {
        if (category != null) {
            viewModel.onEvent(AddOrEditCategoryFormEvent.CategoryInitialized(category))
        }
    }

    AddOrEditCategoryDialog(
        state = viewModel.state,
        onColorChange = { colorItem ->
            scope.launch {
                viewModel.onEvent(AddOrEditCategoryFormEvent.CategoryColorChanged(colorItem))
            }
        },
        onNameChange = { name ->
            scope.launch {
                viewModel.onEvent(AddOrEditCategoryFormEvent.CategoryNameChanged(name))
            }
        },
        onSubmit = {
            scope.launch {
                viewModel.onEvent(AddOrEditCategoryFormEvent.Submit)
                onDismiss()
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun AddOrEditCategoryDialog(
    state: AddOrEditCategoryState,
    onColorChange: (ColorItem) -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        modifier = modifier.testTag(TestTags.ADD_EDIT_CATEGORY_DIALOG),
        title = {
            Text(
                text = if (state.id != null) {
                    stringResource(R.string.category_manager_edit_category)
                } else {
                    stringResource(R.string.category_manager_add_category)
                }
            )
        }, confirmButton = {
            TextButton(
                enabled = state.isValid,
                modifier = Modifier.testTag(TestTags.CATEGORY_SAVE_BUTTON),
                onClick = {
                    onSubmit()
                }) {
                Text(
                    text = stringResource(R.string.editor_button_save)
                )
            }
        }, dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(android.R.string.cancel)
                )
            }
        }, text = {
            AddOrEditCategoryForm(
                state = state, onColorChange = onColorChange, onNameChange = onNameChange
            )
        })
}

@PreviewLightDark
@Composable
private fun PreviewAddOrEditCategoryDialog_Add() {
    LyricCastTheme {
        Surface {
            AddOrEditCategoryDialog(
                state = MutableAddOrEditCategoryState().apply {
                    id = null
                    name = ""
                    color = ColorItem(
                        name = UiText.DynamicString("Red"), value = Color.Red.toArgb()
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewAddOrEditCategoryDialog_Edit() {
    LyricCastTheme {
        Surface {
            AddOrEditCategoryDialog(
                state = MutableAddOrEditCategoryState().apply {
                    name = "Sample Category"
                    color = ColorItem(
                        name = UiText.DynamicString("Red"), value = Color.Red.toArgb()
                    )
                }
            )
        }
    }
}
