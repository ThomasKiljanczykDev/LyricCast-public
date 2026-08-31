
package dev.thomas_kiljanczyk.lyriccast.feature.main.impl.ui.setlists

import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import dev.thomas_kiljanczyk.lyriccast.common.io.UriStreamDataSource
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.DeleteSetlistsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main.ExportSetlistsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteSetlistsResult
import dev.thomas_kiljanczyk.lyriccast.core.model.SetlistItem
import dev.thomas_kiljanczyk.lyriccast.core.ui.list.SelectionListController
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

interface SetlistsScreenState {
    val searchQuery: String
    val isInSelectionMode: Boolean
    val selectedSetlists: ImmutableList<SetlistItem>
    val isExporting: Boolean
    val setlists: ImmutableList<SetlistItem>
    val filteredSetlists: ImmutableList<SetlistItem>
}

private val SEARCH_DEBOUNCE = 300.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class SetlistsScreenViewModel @Inject constructor(
    setlistsRepository: SetlistsRepository,
    private val exportSetlistsUseCase: ExportSetlistsUseCase,
    private val uriStreams: UriStreamDataSource,
    private val deleteSetlistsUseCase: DeleteSetlistsUseCase,
    @param:Dispatcher(LyricCastDispatchers.Default)
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel(), SetlistsScreenState {

    private val controller = SelectionListController<SetlistItem>(
        idOf = { it.id },
        withSelection = { item, selected -> item.copy(isSelected = selected) },
        sort = { it.sorted() }
    )

    override var searchQuery: String by mutableStateOf("")
        private set

    private var debouncedSearchQuery: String by mutableStateOf("")

    override var isExporting: Boolean by mutableStateOf(false)
        private set

    override val setlists: ImmutableList<SetlistItem> get() = controller.projectedItems
    override val isInSelectionMode: Boolean get() = controller.isInSelectionMode
    override val selectedSetlists: ImmutableList<SetlistItem> get() = controller.selectedItems

    override val filteredSetlists: ImmutableList<SetlistItem> by derivedStateOf {
        val normalizedQuery = debouncedSearchQuery.normalize()

        setlists.filter {
            it.normalizedName.contains(normalizedQuery, ignoreCase = true)
        }.toImmutableList()
    }

    val state: SetlistsScreenState get() = this

    private val searchQueryFlow = MutableStateFlow("")

    init {
        searchQueryFlow.onEach { query ->
            searchQuery = query
        }.launchIn(viewModelScope)

        searchQueryFlow.debounce(SEARCH_DEBOUNCE).onEach {
            debouncedSearchQuery = searchQuery
        }.launchIn(viewModelScope)

        controller.bind(
            viewModelScope,
            setlistsRepository.getAllSetlists()
                .map { allSetlists ->
                    withContext(defaultDispatcher) {
                        allSetlists.map { SetlistItem.fromSetlist(it) }
                    }
                }
        )
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    fun enterSelectionMode() = controller.enterSelectionMode()

    fun exitSelectionMode() = controller.exitSelectionMode()

    fun toggleSetlistSelection(setlistItem: SetlistItem) =
        controller.toggleSelection(setlistItem.id)

    suspend fun deleteSelectedSetlists(): DeleteSetlistsResult {
        val selectedSetlistIds = controller.selectedItems.map { it.id }
        val result = deleteSetlistsUseCase(selectedSetlistIds)

        if (result is DeleteSetlistsResult.Success) {
            exitSelectionMode()
        }

        return result
    }

    suspend fun exportSelectedSetlists(destination: Uri) {
        isExporting = true
        try {
            val cacheDir = uriStreams.cacheDirPath()
            uriStreams.withOutputStream(destination) { outputStream ->
                exportSetlistsUseCase(cacheDir, outputStream, controller.selectedItems).collect()
            }
        } finally {
            isExporting = false
        }
    }
}
