/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:33 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.setlist_editor.songs

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.GetAllSongsForSelectionUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.shared.GetCategoriesWithNullOptionUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.MutableSongFilterState
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.SongFilterState
import java.util.UUID
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface SetlistSongSelectionDialogState {
    val allAvailableSongs: ImmutableList<SongItem>
    val filteredAvailableSongs: ImmutableList<SongItem>
    val categories: ImmutableList<CategoryItem?>
    val filterState: SongFilterState
}

class MutableSetlistSongSelectionDialogState : SetlistSongSelectionDialogState {
    override var allAvailableSongs by mutableStateOf<PersistentList<SongItem>>(persistentListOf())
    override var categories by mutableStateOf<ImmutableList<CategoryItem?>>(persistentListOf(null))
    override val filterState = MutableSongFilterState()
    val debouncedFilterState = MutableSongFilterState()

    override val filteredAvailableSongs by derivedStateOf {
        debouncedFilterState.filterSongs(allAvailableSongs).sorted().toImmutableList()
    }
}

private const val SEARCH_DEBOUNCE_MS = 300L

@OptIn(FlowPreview::class)
@HiltViewModel
class SetlistSongSelectionDialogViewModel @Inject constructor(
    private val getAllSongsForSelectionUseCase: GetAllSongsForSelectionUseCase,
    getCategoriesWithNullOptionUseCase: GetCategoriesWithNullOptionUseCase
) : ViewModel() {

    private val _state = MutableSetlistSongSelectionDialogState()
    val state: SetlistSongSelectionDialogState get() = _state

    private val searchQueryFlow = MutableStateFlow("")

    private var initialSongIds: Set<UUID> = emptySet()

    init {
        searchQueryFlow.onEach { query ->
            _state.filterState.searchText = query
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE_MS).onEach {
            _state.debouncedFilterState.searchText = _state.filterState.searchText
        }.launchIn(viewModelScope)

        // Initialize song selection data
        getAllSongsForSelectionUseCase(initialSongIds)
            .onEach { songItems ->
                _state.allAvailableSongs = songItems.toPersistentList()
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)

        getCategoriesWithNullOptionUseCase()
            .onEach { categoryItems ->
                _state.categories = categoryItems
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun updateSearchText(text: String) {
        searchQueryFlow.value = text
    }

    fun updateSelectedCategory(category: CategoryItem?) {
        _state.filterState.selectedCategory = category
        _state.debouncedFilterState.selectedCategory = category
    }

    fun updateShowOnlySelected(show: Boolean) {
        _state.filterState.showOnlySelected = show
        _state.debouncedFilterState.showOnlySelected = show
    }

    fun setInitialSelection(songIds: List<UUID>) {
        initialSongIds = songIds.toSet()
        // Restart the flow with new initial selection
        getAllSongsForSelectionUseCase(initialSongIds)
            .onEach { songItems ->
                _state.allAvailableSongs = songItems.toPersistentList()
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun selectAvailableSong(songItem: SongItem) {
        val songIndex = _state.allAvailableSongs.indexOfFirst { it.id == songItem.id }
        if (songIndex == -1) return

        val currentSongItem = _state.allAvailableSongs[songIndex]
        _state.allAvailableSongs = _state.allAvailableSongs.set(
            songIndex, currentSongItem.copy(isSelected = !currentSongItem.isSelected)
        )
    }

    fun getSelectedSongIds(): List<UUID> {
        return _state.allAvailableSongs.filter { it.isSelected }.map { it.id }
    }
}
