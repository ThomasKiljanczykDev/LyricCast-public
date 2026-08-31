
package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.songs

import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.common.io.UriStreamDataSource
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.DeleteSongsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.ExportSongsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.GetAllSongsForSelectionUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteSongsResult
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.list.SelectionListController
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.MutableSongFilterState
import dev.thomas_kiljanczyk.lyriccast.core.ui.state.SongFilterState
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface SongsScreenState {
    val isInSelectionMode: Boolean
    val selectedSongs: List<SongItem>
    val isExporting: Boolean
    val filterState: SongFilterState
    val songs: ImmutableList<SongItem>
    val filteredSongs: ImmutableList<SongItem>
}

private val SEARCH_DEBOUNCE = 300.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SongsScreenViewModel @Inject constructor(
    getAllSongsForSelectionUseCase: GetAllSongsForSelectionUseCase,
    categoriesRepository: CategoriesRepository,
    private val exportSongsUseCase: ExportSongsUseCase,
    private val uriStreams: UriStreamDataSource,
    private val deleteSongsUseCase: DeleteSongsUseCase
) : ViewModel(), SongsScreenState {

    private val controller = SelectionListController<SongItem>(
        idOf = { it.id },
        withSelection = { item, selected -> item.copy(isSelected = selected) }
        // No sort here: GetAllSongsForSelectionUseCase already emits songs in canonical
        // (title) order and the filter preserves it, so re-sorting would be redundant.
    )

    private val mutableFilterState = MutableSongFilterState()
    private val debouncedFilterState = MutableSongFilterState()

    override val filterState: SongFilterState get() = mutableFilterState

    override var isExporting: Boolean by mutableStateOf(false)
        private set

    override val songs: ImmutableList<SongItem> get() = controller.projectedItems
    override val isInSelectionMode: Boolean get() = controller.isInSelectionMode
    override val selectedSongs: List<SongItem> get() = controller.selectedItems

    override val filteredSongs: ImmutableList<SongItem> by derivedStateOf {
        debouncedFilterState.filterSongs(songs).sorted().toImmutableList()
    }

    val state: SongsScreenState get() = this

    private val searchQueryFlow = MutableStateFlow("")
    private val selectedCategoryFlow = MutableStateFlow<CategoryItem?>(null)

    init {
        searchQueryFlow.onEach { query ->
            mutableFilterState.searchText = query
        }.launchIn(viewModelScope)

        selectedCategoryFlow.onEach { category ->
            mutableFilterState.selectedCategory = category
            debouncedFilterState.selectedCategory = category
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE).onEach {
            debouncedFilterState.searchText = mutableFilterState.searchText
        }.launchIn(viewModelScope)

        controller.bind(viewModelScope, getAllSongsForSelectionUseCase())
    }

    val categories = categoriesRepository.getAllCategories()

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun selectCategory(category: CategoryItem?) {
        selectedCategoryFlow.value = category
    }

    fun enterSelectionMode() = controller.enterSelectionMode()

    fun exitSelectionMode() = controller.exitSelectionMode()

    fun toggleSongSelection(songItem: SongItem) = controller.toggleSelection(songItem.id)

    suspend fun deleteSelectedSongs(): DeleteSongsResult {
        val selectedSongIds = controller.selectedItems.map { it.id }
        val result = deleteSongsUseCase(selectedSongIds)

        if (result is DeleteSongsResult.Success) {
            exitSelectionMode()
        }

        return result
    }

    suspend fun exportSelectedSongs(destination: Uri) {
        isExporting = true
        try {
            val cacheDir = uriStreams.cacheDirPath()
            uriStreams.withOutputStream(destination) { outputStream ->
                exportSongsUseCase(cacheDir, outputStream, controller.selectedItems).collect()
            }
        } finally {
            isExporting = false
        }
    }
}
