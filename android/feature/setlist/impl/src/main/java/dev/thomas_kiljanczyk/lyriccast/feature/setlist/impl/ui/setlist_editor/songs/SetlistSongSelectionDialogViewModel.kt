
package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor.songs

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
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface SetlistSongSelectionDialogState {
    val allAvailableSongs: ImmutableList<SongItem>
    val filteredAvailableSongs: ImmutableList<SongItem>
    val categories: ImmutableList<CategoryItem?>
    val filterState: SongFilterState
}

/** Plain holder used by previews and tests; the ViewModel implements the interface itself. */
class MutableSetlistSongSelectionDialogState : SetlistSongSelectionDialogState {
    override var allAvailableSongs by mutableStateOf<ImmutableList<SongItem>>(persistentListOf())
    override var filteredAvailableSongs by mutableStateOf<ImmutableList<SongItem>>(
        persistentListOf()
    )
    override var categories by mutableStateOf<ImmutableList<CategoryItem?>>(persistentListOf(null))
    override val filterState = MutableSongFilterState()
}

private val SEARCH_DEBOUNCE = 300.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SetlistSongSelectionDialogViewModel @Inject constructor(
    getAllSongsForSelectionUseCase: GetAllSongsForSelectionUseCase,
    getCategoriesWithNullOptionUseCase: GetCategoriesWithNullOptionUseCase
) : ViewModel(), SetlistSongSelectionDialogState {

    private var availableSongs: PersistentList<SongItem> by mutableStateOf(persistentListOf())
    override val allAvailableSongs: ImmutableList<SongItem> get() = availableSongs

    override var categories: ImmutableList<CategoryItem?> by mutableStateOf(persistentListOf(null))
        private set

    private val mutableFilterState = MutableSongFilterState()
    private val debouncedFilterState = MutableSongFilterState()

    override val filterState: SongFilterState get() = mutableFilterState

    override val filteredAvailableSongs: ImmutableList<SongItem> by derivedStateOf {
        debouncedFilterState.filterSongs(availableSongs).sorted().toImmutableList()
    }

    val state: SetlistSongSelectionDialogState get() = this

    private val searchQueryFlow = MutableStateFlow("")

    private val initialSongIds = MutableStateFlow<Set<UUID>>(emptySet())

    init {
        searchQueryFlow.onEach { query ->
            mutableFilterState.searchText = query
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE).onEach {
            debouncedFilterState.searchText = mutableFilterState.searchText
        }.launchIn(viewModelScope)

        initialSongIds
            .flatMapLatest { ids -> getAllSongsForSelectionUseCase(ids) }
            .onEach { songItems -> availableSongs = songItems.toPersistentList() }
            .launchIn(viewModelScope)

        getCategoriesWithNullOptionUseCase()
            .onEach { categoryItems -> categories = categoryItems }
            .launchIn(viewModelScope)
    }

    fun updateSearchText(text: String) {
        searchQueryFlow.value = text
    }

    fun updateSelectedCategory(category: CategoryItem?) {
        mutableFilterState.selectedCategory = category
        debouncedFilterState.selectedCategory = category
    }

    fun updateShowOnlySelected(show: Boolean) {
        mutableFilterState.showOnlySelected = show
        debouncedFilterState.showOnlySelected = show
    }

    fun setInitialSelection(songIds: List<UUID>) {
        initialSongIds.value = songIds.toSet()
    }

    fun selectAvailableSong(songItem: SongItem) {
        val songIndex = availableSongs.indexOfFirst { it.id == songItem.id }
        if (songIndex == -1) return

        val currentSongItem = availableSongs[songIndex]
        availableSongs = availableSongs.replacingAt(
            songIndex, currentSongItem.copy(isSelected = !currentSongItem.isSelected)
        )
    }

    fun getSelectedSongIds(): List<UUID> {
        return availableSongs.filter { it.isSelected }.map { it.id }
    }
}
