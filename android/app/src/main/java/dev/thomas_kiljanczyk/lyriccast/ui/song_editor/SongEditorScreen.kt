/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.song_editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.core.designsystem.theme.LyricCastTheme
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.CategoryDropdown
import dev.thomas_kiljanczyk.lyriccast.core.ui.components.LyricCastTextField
import dev.thomas_kiljanczyk.lyriccast.ui.shared.preview.PreviewData
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Composable
fun SongEditorScreen(
    onNavigateUp: () -> Unit,
    songId: UUID? = null,
    viewModel: SongEditorViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    // Load song if songId is provided
    val newSectionTemplate = stringResource(R.string.song_editor_input_new_section_template)
    LaunchedEffect(songId) {
        if (songId != null) {
            viewModel.loadSong(songId)
        } else {
            viewModel.addNewSection(newSectionTemplate)
        }
    }

    SongEditorScreen(
        state = viewModel.state,
        onNavigateUp = onNavigateUp,
        onSongTitleChange = { viewModel.setSongTitle(it) },
        onCategorySelect = { viewModel.setCategory(it) },
        onLyricsSectionNameChange = {
            val currentIndex = viewModel.state.currentSectionIndex
            viewModel.updateSectionName(currentIndex, it)
        },
        onLyricsSectionContentChange = { viewModel.setSectionText(it) },
        onSectionSelect = { viewModel.selectSection(it) },
        onMoveSectionLeft = { scrollCallback ->
            viewModel.moveSectionLeft()
            coroutineScope.launch {
                scrollCallback()
            }
        },
        onMoveSectionRight = { scrollCallback ->
            viewModel.moveSectionRight()
            coroutineScope.launch {
                scrollCallback()
            }
        },
        onDeleteSection = { viewModel.deleteCurrentSection() },
        onAddNewSection = { sectionName, scrollCallback ->
            viewModel.addNewSection(sectionName)
            coroutineScope.launch {
                scrollCallback()
            }
        },
        onSave = {
            coroutineScope.launch {
                viewModel.saveSong()
                onNavigateUp()
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongEditorScreen(
    state: SongEditorState,
    onNavigateUp: () -> Unit = {},
    onSongTitleChange: (String) -> Unit = {},
    onCategorySelect: (CategoryItem?) -> Unit = {},
    onLyricsSectionNameChange: (String) -> Unit = {},
    onLyricsSectionContentChange: (String) -> Unit = {},
    onSectionSelect: (Int) -> Unit = {},
    onMoveSectionLeft: (suspend () -> Unit) -> Unit = { _ -> },
    onMoveSectionRight: (suspend () -> Unit) -> Unit = { _ -> },
    onDeleteSection: () -> Unit = {},
    onAddNewSection: (String, suspend () -> Unit) -> Unit = { _, _ -> },
    onSave: () -> Unit = {}
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.title_song_editor)) },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_up)
                    )
                }
            },
            actions = {
                FilledTonalButton(
                    onClick = onSave,
                    enabled = state.canSave,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(stringResource(R.string.action_save))
                }
            })
    }) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                LyricCastTextField(
                    label = stringResource(R.string.hint_song_title),
                    value = state.songTitle,
                    onValueChange = onSongTitleChange,
                    maxLength = 50,
                    errorText = when (state.titleValidationState) {
                        NameValidationState.EMPTY -> stringResource(R.string.song_editor_enter_title)
                        NameValidationState.ALREADY_IN_USE -> stringResource(R.string.song_editor_title_already_used)
                        else -> null
                    }
                )

                SongEditorCategoryDropdown(
                    categories = state.categories,
                    selectedCategory = state.songCategory,
                    onCategorySelected = onCategorySelect,
                    modifier = Modifier.fillMaxWidth()
                )

                SongEditorLyricsSections(
                    state = state,
                    sectionName = state.lyricsSectionName,
                    sectionContent = state.lyricsSectionContent,
                    sections = state.lyricsSections,
                    currentSectionIndex = state.currentSectionIndex,
                    onSectionNameChange = onLyricsSectionNameChange,
                    onSectionContentChange = onLyricsSectionContentChange,
                    onMoveSectionLeft = onMoveSectionLeft,
                    onMoveSectionRight = onMoveSectionRight,
                    onDeleteSection = onDeleteSection,
                    onSectionSelect = onSectionSelect,
                    onAddNewSection = onAddNewSection,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SongEditorCategoryDropdown(
    categories: ImmutableList<CategoryItem?>,
    selectedCategory: CategoryItem?,
    onCategorySelected: (CategoryItem?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val categoryColor = selectedCategory?.color

        // Animate the color transition
        val animatedColor by animateColorAsState(
            targetValue = categoryColor?.let { Color(it) } ?: Color.Transparent,
            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            label = "category_color"
        )

        AnimatedVisibility(
            visible = categoryColor != null,
            enter = fadeIn(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()) + expandHorizontally(
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
            ),
            exit = fadeOut(animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()) + shrinkHorizontally(
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
            )
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = animatedColor
                ),
                modifier = Modifier
                    .padding(end = 12.dp)
                    .height(32.dp)
                    .width(32.dp)
            ) {}
        }

        CategoryDropdown(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            nullCategoryText = stringResource(R.string.category_none),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@PreviewLightDark
private fun PreviewSongEditorCategoryDropdownWithSelection() {
    LyricCastTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val categories = PreviewData.sampleCategories.takeLast(3).toImmutableList()

            var selectedCategory by remember { mutableStateOf<CategoryItem?>(categories[1]) }

            SongEditorCategoryDropdown(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun PreviewSongEditorCategoryDropdownNoSelection() {
    LyricCastTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val categories = PreviewData.sampleCategories.takeLast(3).toImmutableList()

            var selectedCategory by remember { mutableStateOf<CategoryItem?>(null) }

            SongEditorCategoryDropdown(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun PreviewSongEditorScreen() {
    val sectionName = PreviewData.amazingGrace.presentation.first()
    LyricCastTheme {
        val previewState = MutableSongEditorState().apply {
            songTitle = PreviewData.amazingGrace.title
            categories = PreviewData.sampleCategories.takeLast(3).toImmutableList()
            lyricsSectionName = sectionName
            lyricsSectionContent = PreviewData.amazingGrace.lyricsMap[sectionName] ?: ""
            lyricsSections = PreviewData.amazingGrace.presentation
            currentSectionIndex = 0
        }
        SongEditorScreen(state = previewState)
    }
}
