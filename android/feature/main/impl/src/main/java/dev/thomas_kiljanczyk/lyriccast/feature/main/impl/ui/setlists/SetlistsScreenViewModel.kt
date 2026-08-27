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

    val state: SetlistsScreenState
        field = MutableSetlistsScreenState()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        searchQueryFlow.onEach { query ->
            state.searchQuery = query
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE).onEach {
            state.debouncedSearchQuery = state.searchQuery
        }.launchIn(viewModelScope)

        setlistsRepository.getAllSetlists().onEach { allSetlists ->
            state.allSetlists = allSetlists.map {
                SetlistItem.fromSetlist(it)
            }.sorted()
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun enterSelectionMode() {
        state.isInSelectionMode = true
    }

    fun exitSelectionMode() {
        state.isInSelectionMode = false
        // Clear all selections
        state.allSetlists = state.allSetlists.map { setlistItem ->
            setlistItem.copy(
                isSelected = false
            )
        }
    }

    fun toggleSetlistSelection(setlistItem: SetlistItem) {
        state.allSetlists = state.allSetlists.map { setlist ->
            if (setlist.id == setlistItem.id) {
                setlist.copy(
                    isSelected = !setlist.isSelected
                )
            } else {
                setlist
            }
        }

        // Exit selection mode if no setlists are selected
        if (state.selectedSetlists.isEmpty() && state.isInSelectionMode) {
            exitSelectionMode()
        }
    }

    suspend fun deleteSelectedSetlists(): DeleteSetlistsResult {
        val selectedSetlistIds = state.selectedSetlists.map { it.id }
        val result = deleteSetlistsUseCase(selectedSetlistIds)

        if (result is DeleteSetlistsResult.Success) {
            exitSelectionMode()
        }

        return result
    }

    fun exportSelectedSetlists(cacheDir: String, outputStream: OutputStream): Flow<Int> =
        flow {
            state.isExporting = true
            try {
                exportSetlistsUseCase(
                    cacheDir,
                    outputStream,
                    state.selectedSetlists
                ).collect { progressResId ->
                    emit(progressResId)
                }
            } finally {
                state.isExporting = false
            }
        }
}
