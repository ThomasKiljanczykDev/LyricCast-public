/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 11:31 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.DeleteSetlistsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.ExportSetlistsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteSetlistsResult
import dev.thomas_kiljanczyk.lyriccast.core.model.SetlistItem
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

interface SetlistsScreenState {
    val searchQuery: String
    val isInSelectionMode: Boolean
    val selectedSetlists: List<SetlistItem>
    val isExporting: Boolean
    val setlists: ImmutableList<SetlistItem>
}

class MutableSetlistsScreenState : SetlistsScreenState {
    override var searchQuery by mutableStateOf("")
    var debouncedSearchQuery by mutableStateOf("")
    override var isInSelectionMode by mutableStateOf(false)
    override var isExporting by mutableStateOf(false)
    var allSetlists by mutableStateOf<List<SetlistItem>>(emptyList())

    override val selectedSetlists by derivedStateOf {
        allSetlists.filter { it.isSelected }
    }

    override val setlists by derivedStateOf {
        val normalizedQuery = debouncedSearchQuery.normalize()

        allSetlists.filter {
            it.normalizedName.contains(normalizedQuery, ignoreCase = true)
        }.sorted().toImmutableList()
    }
}

private val SEARCH_DEBOUNCE = 300.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SetlistsScreenViewModel @Inject constructor(
    setlistsRepository: SetlistsRepository,
    private val exportSetlistsUseCase: ExportSetlistsUseCase,
    private val deleteSetlistsUseCase: DeleteSetlistsUseCase
) : ViewModel() {

    private val _state = MutableSetlistsScreenState()
    val state: SetlistsScreenState get() = _state

    private val searchQueryFlow = MutableStateFlow("")

    init {
        searchQueryFlow.onEach { query ->
            _state.searchQuery = query
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE).onEach {
            _state.debouncedSearchQuery = _state.searchQuery
        }.launchIn(viewModelScope)

        setlistsRepository.getAllSetlists().onEach { allSetlists ->
            _state.allSetlists = allSetlists.map {
                SetlistItem.fromSetlist(it)
            }.sorted()
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun enterSelectionMode() {
        _state.isInSelectionMode = true
    }

    fun exitSelectionMode() {
        _state.isInSelectionMode = false
        // Clear all selections
        _state.allSetlists = _state.allSetlists.map { setlistItem ->
            setlistItem.copy(
                isSelected = false
            )
        }
    }

    fun toggleSetlistSelection(setlistItem: SetlistItem) {
        _state.allSetlists = _state.allSetlists.map { setlist ->
            if (setlist.id == setlistItem.id) {
                setlist.copy(
                    isSelected = !setlist.isSelected
                )
            } else {
                setlist
            }
        }

        // Exit selection mode if no setlists are selected
        if (_state.selectedSetlists.isEmpty() && _state.isInSelectionMode) {
            exitSelectionMode()
        }
    }

    suspend fun deleteSelectedSetlists(): DeleteSetlistsResult {
        val selectedSetlistIds = _state.selectedSetlists.map { it.id }
        val result = deleteSetlistsUseCase(selectedSetlistIds)

        if (result is DeleteSetlistsResult.Success) {
            exitSelectionMode()
        }

        return result
    }

    fun exportSelectedSetlists(cacheDir: String, outputStream: OutputStream): Flow<Int> =
        flow {
            _state.isExporting = true
            try {
                exportSetlistsUseCase(
                    cacheDir,
                    outputStream,
                    _state.selectedSetlists
                ).collect { progressResId ->
                    emit(progressResId)
                }
            } finally {
                _state.isExporting = false
            }
        }
}
