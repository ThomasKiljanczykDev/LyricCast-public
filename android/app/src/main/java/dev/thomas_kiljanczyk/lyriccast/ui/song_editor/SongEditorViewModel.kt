/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 7:11 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 6:34 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.song_editor

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import dev.thomas_kiljanczyk.lyriccast.domain.models.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.domain.models.LoadSongResult
import dev.thomas_kiljanczyk.lyriccast.domain.models.SaveSongResult
import dev.thomas_kiljanczyk.lyriccast.domain.use_case.shared.GetCategoriesWithNullOptionUseCase
import dev.thomas_kiljanczyk.lyriccast.domain.use_case.song_editor.GetSongTitlesUseCase
import dev.thomas_kiljanczyk.lyriccast.domain.use_case.song_editor.LoadSongUseCase
import dev.thomas_kiljanczyk.lyriccast.domain.use_case.song_editor.SaveSongUseCase
import dev.thomas_kiljanczyk.lyriccast.domain.use_case.song_editor.ValidateSongTitleUseCase
import dev.thomas_kiljanczyk.lyriccast.shared.enums.NameValidationState
import java.util.UUID
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class LyricsSection(
    val name: String, val content: String = ""
)

interface SongEditorState {
    val categories: ImmutableList<CategoryItem?>
    val songTitle: String
    val songCategory: CategoryItem?
    val lyricsSectionName: String
    val lyricsSectionContent: String
    val lyricsSections: List<String>
    val currentSectionIndex: Int
    val titleValidationState: NameValidationState
    val sectionNameValidationState: NameValidationState
    val canSave: Boolean
}

class MutableSongEditorState : SongEditorState {
    override var categories by mutableStateOf<ImmutableList<CategoryItem?>>(persistentListOf(null))
    override var songTitle by mutableStateOf("")
    override var songCategory by mutableStateOf<CategoryItem?>(null)
    override var lyricsSectionName by mutableStateOf("")
    override var lyricsSectionContent by mutableStateOf("")
    override var lyricsSections by mutableStateOf<List<String>>(listOf())
    override var currentSectionIndex by mutableIntStateOf(0)
    override var titleValidationState by mutableStateOf(NameValidationState.VALID)
    override var sectionNameValidationState by mutableStateOf(NameValidationState.VALID)

    override val canSave by derivedStateOf {
        titleValidationState == NameValidationState.VALID &&
            sectionNameValidationState == NameValidationState.VALID &&
            songTitle.isNotBlank() &&
            lyricsSections.all { it.isNotBlank() }
    }
}

@HiltViewModel
class SongEditorViewModel @Inject constructor(
    getCategoriesWithNullOptionUseCase: GetCategoriesWithNullOptionUseCase,
    getSongTitlesUseCase: GetSongTitlesUseCase,
    private val validateSongTitleUseCase: ValidateSongTitleUseCase,
    private val saveSongUseCase: SaveSongUseCase,
    private val loadSongUseCase: LoadSongUseCase
) : ViewModel() {

    private companion object {
        const val TAG = "SongEditorModel"
    }

    private var songId: UUID? = null

    private var editedSong: Song? = null

    private var songTitles: Set<String> = setOf()

    private val _state = MutableSongEditorState()
    val state: SongEditorState get() = _state

    private val sections: MutableList<LyricsSection> = mutableListOf()

    init {
        getSongTitlesUseCase()
            .onEach { titles -> songTitles = titles }
            .flowOn(Dispatchers.Default).launchIn(viewModelScope)

        getCategoriesWithNullOptionUseCase()
            .onEach { categoryItems -> _state.categories = categoryItems }
            .flowOn(Dispatchers.Default).launchIn(viewModelScope)
    }

    fun setSongTitle(newSongTitle: String) {
        val validationState = validateSongTitle(newSongTitle)

        _state.apply {
            songTitle = newSongTitle
            titleValidationState = validationState
        }
    }

    fun setCategory(categoryItem: CategoryItem?) {
        _state.songCategory = categoryItem
    }

    fun selectSection(index: Int) {
        if (index !in sections.indices) return

        val section = sections[index]
        val validationState = validateSectionName(section.name)

        _state.apply {
            currentSectionIndex = index
            lyricsSectionName = section.name
            lyricsSectionContent = section.content
            lyricsSections = sections.map { it.name }
            sectionNameValidationState = validationState
        }
    }

    fun moveSectionLeft() {
        val currentIndex = state.currentSectionIndex

        if (currentIndex <= 0 || sections.isEmpty()) return

        // Swap with previous section
        val section = sections.removeAt(currentIndex)
        sections.add(currentIndex - 1, section)

        _state.apply {
            currentSectionIndex = currentIndex - 1
            lyricsSections = sections.map { it.name }
        }
    }

    fun moveSectionRight() {
        val currentIndex = state.currentSectionIndex

        if (currentIndex >= sections.size - 1 || sections.isEmpty()) return

        // Swap with next section
        val section = sections.removeAt(currentIndex)
        sections.add(currentIndex + 1, section)

        _state.apply {
            currentSectionIndex = currentIndex + 1
            lyricsSections = sections.map { it.name }
        }
    }

    fun deleteCurrentSection() {
        // Don't allow deleting the last section
        if (sections.size <= 1) return

        val currentIndex = state.currentSectionIndex
        if (currentIndex !in sections.indices) return

        sections.removeAt(currentIndex)

        val newIndex = currentIndex.coerceAtMost(sections.size - 1).coerceAtLeast(0)
        if (sections.isNotEmpty()) {
            selectSection(newIndex)
        }

        _state.lyricsSections = sections.map { it.name }
    }

    fun addNewSection(name: String) {
        val newSection = LyricsSection(
            name = name, content = ""
        )
        sections.add(newSection)

        val newIndex = sections.size - 1

        _state.apply {
            lyricsSections = sections.map { it.name }
            lyricsSectionName = newSection.name
            lyricsSectionContent = newSection.content
            currentSectionIndex = newIndex
        }
    }

    fun validateSongTitle(songTitle: String): NameValidationState {
        return validateSongTitleUseCase(
            songTitle = songTitle,
            existingTitles = songTitles,
            currentTitle = editedSong?.title
        )
    }

    suspend fun loadSong(songId: UUID) {
        when (val result = loadSongUseCase(songId)) {
            is LoadSongResult.Success -> {
                val song = result.song
                editedSong = song
                this.songId = song.id

                // Clear existing sections
                sections.clear()
                sections.addAll(result.sections)

                // Set category in state
                val categoryItem = song.category?.let { CategoryItem(it) }

                _state.apply {
                    songCategory = categoryItem
                    songTitle = song.title
                }

                // Select first section if available
                if (sections.isNotEmpty()) {
                    selectSection(0)
                }
            }

            is LoadSongResult.Error -> {
                Log.e(TAG, "Failed to load song: ${result.message}")
            }
        }
    }

    suspend fun saveSong(): SaveSongResult {
        return saveSongUseCase(
            songId = songId,
            title = state.songTitle,
            sections = sections,
            category = _state.songCategory,
            existingTitles = songTitles,
            currentTitle = editedSong?.title
        )
    }

    fun updateSectionName(index: Int, newSectionName: String) {
        if (index !in sections.indices) return

        val validationState = validateSectionName(newSectionName)
        val section = sections[index]

        // Just update validation state if name hasn't changed
        if (section.name == newSectionName) {
            _state.sectionNameValidationState = validationState
            return
        }

        val existingSectionWithSameName = sections.find { it.name == newSectionName }

        // Update the section name only if it's valid, but always update the UI state
        if (validationState == NameValidationState.VALID) {
            sections[index] = section.copy(
                name = newSectionName,
                content = existingSectionWithSameName?.content ?: section.content
            )
        }

        _state.apply {
            lyricsSectionName = newSectionName
            sectionNameValidationState = validationState
            if (validationState == NameValidationState.VALID) {
                lyricsSections = sections.map { it.name }
            }
            if (existingSectionWithSameName != null) {
                lyricsSectionContent = existingSectionWithSameName.content
            }
        }
    }

    private fun validateSectionName(sectionName: String): NameValidationState {
        if (sectionName.isBlank()) {
            return NameValidationState.EMPTY
        }
        return NameValidationState.VALID
    }

    fun setSectionText(sectionText: String) {
        val currentIndex = state.currentSectionIndex

        if (currentIndex !in sections.indices) return

        sections[currentIndex] = sections[currentIndex].copy(content = sectionText)
        _state.lyricsSectionContent = sectionText
    }
}
