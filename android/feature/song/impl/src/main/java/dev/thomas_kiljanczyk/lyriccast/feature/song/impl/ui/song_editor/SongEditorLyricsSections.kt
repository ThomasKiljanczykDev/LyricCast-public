
package dev.thomas_kiljanczyk.lyriccast.feature.song.impl.ui.song_editor

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.TourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.tourAnchor
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastTextField
import dev.thomas_kiljanczyk.lyriccast.core.ui.util.currentWindowSizeClass
import dev.thomas_kiljanczyk.lyriccast.feature.song.impl.R
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SongEditorLyricsSections(
    state: SongEditorState,
    sectionName: String,
    sectionContent: String,
    sections: List<String>,
    currentSectionIndex: Int,
    onSectionNameChange: (String) -> Unit,
    onSectionContentChange: (String) -> Unit,
    onMoveSectionLeft: (suspend () -> Unit) -> Unit,
    onMoveSectionRight: (suspend () -> Unit) -> Unit,
    onDeleteSection: () -> Unit,
    onSectionSelect: (Int) -> Unit,
    onAddNewSection: (String, suspend () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowSizeClass()
) {
    val lazyListState = rememberLazyListState()

    @Composable
    fun SectionNameField(fieldModifier: Modifier = Modifier) {
        LyricCastTextField(
            label = stringResource(R.string.song_editor_input_section_name),
            value = sectionName,
            onValueChange = onSectionNameChange,
            maxLength = 30,
            modifier = fieldModifier.fillMaxWidth(),
            textAllCaps = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters
            ),
            errorText = when (state.sectionNameValidationState) {
                NameValidationState.EMPTY -> stringResource(R.string.song_editor_enter_section_name)
                else -> null
            }
        )
    }

    @Composable
    fun LyricsContentField(fieldModifier: Modifier = Modifier) {
        Card(
            modifier = fieldModifier
                .fillMaxWidth()
                .tourAnchor(TourAnchor.SONG_EDITOR_LYRICS),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            LyricCastTextField(
                placeholder = stringResource(R.string.song_editor_hint_enter_lyrics),
                modifier = Modifier.fillMaxSize(),
                value = sectionContent,
                onValueChange = onSectionContentChange,
                singleLine = false,
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )
        }
    }

    Card(
        modifier = modifier, colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        if (windowSizeClass.useTwoPaneEditorLayout()) {
            // List-detail layout also relieves vertical pressure on short windows.
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    SectionCards(
                        sections = sections,
                        currentSectionIndex = currentSectionIndex,
                        onSectionSelect = onSectionSelect,
                        onAddNewSection = onAddNewSection,
                        lazyListState = lazyListState,
                        vertical = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    SectionControls(
                        sections = sections,
                        currentSectionIndex = currentSectionIndex,
                        onMoveSectionLeft = onMoveSectionLeft,
                        onMoveSectionRight = onMoveSectionRight,
                        onDeleteSection = onDeleteSection,
                        lazyListState = lazyListState,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    SectionNameField()

                    LyricsContentField(Modifier.weight(1f))
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                SectionNameField()

                LyricsContentField(Modifier.weight(1f))

                SectionControls(
                    sections = sections,
                    currentSectionIndex = currentSectionIndex,
                    onMoveSectionLeft = onMoveSectionLeft,
                    onMoveSectionRight = onMoveSectionRight,
                    onDeleteSection = onDeleteSection,
                    lazyListState = lazyListState,
                    modifier = Modifier.fillMaxWidth()
                )

                SectionCards(
                    sections = sections,
                    currentSectionIndex = currentSectionIndex,
                    onSectionSelect = onSectionSelect,
                    onAddNewSection = onAddNewSection,
                    lazyListState = lazyListState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SectionControls(
    sections: List<String>,
    currentSectionIndex: Int,
    onMoveSectionLeft: (suspend () -> Unit) -> Unit,
    onMoveSectionRight: (suspend () -> Unit) -> Unit,
    onDeleteSection: () -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FilledIconButton(
            onClick = {
                onMoveSectionLeft {
                    lazyListState.animateScrollToItem((currentSectionIndex - 1).coerceAtLeast(0))
                }
            }, enabled = currentSectionIndex > 0 && sections.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.song_editor_button_move_left)
            )
        }
        Button(
            onClick = onDeleteSection,
            enabled = sections.size > 1,
            colors = ButtonDefaults.buttonColors().copy(
                contentColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(stringResource(R.string.song_editor_button_delete_section))
        }
        FilledIconButton(
            onClick = {
                onMoveSectionRight {
                    lazyListState.animateScrollToItem(
                        (currentSectionIndex + 1).coerceAtMost(sections.size - 1)
                    )
                }
            }, enabled = currentSectionIndex < sections.size - 1 && sections.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = stringResource(R.string.song_editor_button_move_right)
            )
        }
    }
}

@Composable
private fun SectionCards(
    sections: List<String>,
    currentSectionIndex: Int,
    onSectionSelect: (Int) -> Unit,
    onAddNewSection: (String, suspend () -> Unit) -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    vertical: Boolean = false
) {
    val newSectionTemplate = stringResource(R.string.song_editor_input_new_section_template)

    // The list is the presentation order, so a chorus sung twice appears twice; the name alone
    // is not a key. Numbering the repeats keeps every key stable across a reorder.
    val sectionKeys = remember(sections) {
        val occurrences = mutableMapOf<String, Int>()
        sections.map { name ->
            val occurrence = occurrences.getOrElse(name) { 0 }
            occurrences[name] = occurrence + 1
            "$name#$occurrence"
        }
    }

    @Composable
    fun SectionChip(index: Int, tab: String, chipModifier: Modifier) {
        val isSelected = index == currentSectionIndex
        val animatedCardColor by animateColorAsState(
            if (isSelected) {
                MaterialTheme.colorScheme.surfaceDim
            } else {
                MaterialTheme.colorScheme.surfaceBright
            },
            label = "CardColor"
        )
        val animatedTextColor by animateColorAsState(
            if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            },
            label = "TextColor"
        )

        Card(
            modifier = chipModifier,
            onClick = { onSectionSelect(index) },
            colors = CardDefaults.cardColors().copy(containerColor = animatedCardColor)
        ) {
            Text(
                tab, color = animatedTextColor, modifier = Modifier.padding(
                    vertical = 8.dp, horizontal = 16.dp
                )
            )
        }
    }

    @Composable
    fun AddButton() {
        FilledIconButton(
            onClick = {
                onAddNewSection(newSectionTemplate) {
                    lazyListState.animateScrollToItem(sections.size)
                }
            },
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.song_editor_input_new_section)
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = modifier.tourAnchor(TourAnchor.SONG_EDITOR_STRUCTURE)
    ) {
        if (vertical) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors().copy(containerColor = Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        state = lazyListState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        itemsIndexed(
                            sections,
                            key = { index, _ -> sectionKeys[index] }
                        ) { index, tab ->
                            SectionChip(
                                index = index,
                                tab = tab,
                                chipModifier = Modifier
                                    .animateItem()
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                AddButton()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors().copy(containerColor = Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    LazyRow(
                        state = lazyListState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                    ) {
                        itemsIndexed(
                            sections,
                            key = { index, _ -> sectionKeys[index] }
                        ) { index, tab ->
                            SectionChip(
                                index = index,
                                tab = tab,
                                chipModifier = Modifier.animateItem()
                            )
                        }
                    }
                }

                AddButton()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSongEditorLyricsSections() {
    LyricCastTheme {
        val previewState = MutableSongEditorState().apply {
            songTitle = "Sample Song"
            categories = persistentListOf(
                CategoryItem(Category(name = "Pop", color = Color.Red.toArgb())),
                CategoryItem(Category(name = "Rock", color = Color.Blue.toArgb())),
                CategoryItem(Category(name = "Jazz", color = Color.Green.toArgb()))
            )
            lyricsSectionName = "Verse 1"
            lyricsSectionContent =
                "These are the lyrics for verse 1\nWith multiple lines of text\nTo show the editor"
            lyricsSections = listOf("Verse 1", "Chorus", "Verse 2", "Bridge", "Outro")
            currentSectionIndex = 2
            titleValidationState = NameValidationState.VALID
            sectionNameValidationState = NameValidationState.VALID
        }

        Surface {
            SongEditorLyricsSections(
                state = previewState,
                sectionName = previewState.lyricsSectionName,
                sectionContent = previewState.lyricsSectionContent,
                sections = previewState.lyricsSections,
                currentSectionIndex = previewState.currentSectionIndex,
                onSectionNameChange = {},
                onSectionContentChange = {},
                onMoveSectionLeft = {},
                onMoveSectionRight = {},
                onDeleteSection = {},
                onSectionSelect = {},
                onAddNewSection = { _, _ -> },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
