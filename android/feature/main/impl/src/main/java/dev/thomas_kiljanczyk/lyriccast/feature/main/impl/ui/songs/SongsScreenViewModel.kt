/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:33 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.DeleteSongsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.ExportSongsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteSongsResult
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.MutableSongFilterState
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.SongFilterState
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.milliseconds

interface SongsScreenState {
    val isInSelectionMode: Boolean
    val selectedSongs: List<SongItem>
    val isExporting: Boolean
    val filterState: SongFilterState
    val songs: List<SongItem>

    // Delegate properties to filter state
    val filteredSongs: ImmutableList<SongItem>
}

class MutableSongsScreenState : SongsScreenState {
    override var isInSelectionMode by mutableStateOf(false)
    override var isExporting by mutableStateOf(false)
    override val filterState = MutableSongFilterState()
    var debouncedFilterState by mutableStateOf(MutableSongFilterState())
    override var songs by mutableStateOf<List<SongItem>>(emptyList())

    override val selectedSongs by derivedStateOf {
        songs.filter { it.isSelected }
    }

    override val filteredSongs by derivedStateOf {
        debouncedFilterState.filterSongs(songs).sorted().toImmutableList()
    }
}

private val SEARCH_DEBOUNCE = 300.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SongsScreenViewModel @Inject constructor(
    songsRepository: SongsRepository,
    categoriesRepository: CategoriesRepository,
    private val exportSongsUseCase: ExportSongsUseCase,
    private val deleteSongsUseCase: DeleteSongsUseCase
) : ViewModel() {

    val state: SongsScreenState
        field = MutableSongsScreenState()

    private val searchQueryFlow = MutableStateFlow("")
    private val selectedCategoryFlow = MutableStateFlow<CategoryItem?>(null)

    init {
        searchQueryFlow.onEach { query ->
            state.filterState.searchText = query
        }.launchIn(viewModelScope)

        selectedCategoryFlow.onEach { category ->
            state.filterState.selectedCategory = category
            state.debouncedFilterState.selectedCategory = category
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE).onEach {
            state.debouncedFilterState.searchText = state.filterState.searchText
        }.launchIn(viewModelScope)

        // Monitor songs and filters
        songsRepository.getAllSongs().onEach { allSongs ->
            // Use filter state to filter songs
            state.songs = allSongs.map {
                SongItem.fromSong(it, false)
            }.sorted()
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    val categories = categoriesRepository.getAllCategories()
        .flowOn(Dispatchers.Default)

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun selectCategory(category: CategoryItem?) {
        selectedCategoryFlow.value = category
    }

    fun enterSelectionMode() {
        state.isInSelectionMode = true
    }

    fun exitSelectionMode() {
        state.isInSelectionMode = false
        // Clear all selections
        state.songs = state.songs.map {
            it.copy(isSelected = false)
        }
    }

    fun toggleSongSelection(songItem: SongItem) {
        state.songs = state.songs.map { song ->
            if (song.id == songItem.id) {
                song.copy(isSelected = !song.isSelected)
            } else {
                song
            }
        }

        // Exit selection mode if no songs are selected
        if (state.selectedSongs.isEmpty() && state.isInSelectionMode) {
            exitSelectionMode()
        }
    }

    suspend fun deleteSelectedSongs(): DeleteSongsResult {
        val selectedSongIds = state.selectedSongs.map { it.id }
        val result = deleteSongsUseCase(selectedSongIds)

        if (result is DeleteSongsResult.Success) {
            exitSelectionMode()
        }

        return result
    }

    fun exportSelectedSongs(cacheDir: String, outputStream: OutputStream): Flow<Int> =
        flow {
            state.isExporting = true
            try {
                exportSongsUseCase(
                    cacheDir,
                    outputStream,
                    state.selectedSongs
                ).collect { progressResId ->
                    emit(progressResId)
                }
            } finally {
                state.isExporting = false
            }
        }
}
