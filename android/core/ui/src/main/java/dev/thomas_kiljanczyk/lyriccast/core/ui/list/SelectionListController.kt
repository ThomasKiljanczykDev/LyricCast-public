package dev.thomas_kiljanczyk.lyriccast.core.ui.list

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Selection + bulk-action algebra shared by list screens.
 *
 * @param T item type that carries an `isSelected` flag.
 *
 * The controller owns:
 * - the items list (sourced from a [Flow] via [bind])
 * - selection mode + selected ids (with auto-exit when selection empties)
 * - the items projection that stamps each `T` with its current `isSelected` flag
 *
 * Filtering and sorting stay with each feature's ViewModel — their filter state
 * shapes differ enough (`SongFilterState` vs. plain accent-stripped string) that
 * abstracting them here adds more friction than it removes.
 */
class SelectionListController<T : Any>(
    private val idOf: (T) -> Any,
    private val withSelection: (T, Boolean) -> T,
    private val sort: (List<T>) -> List<T> = { it }
) {
    var items: PersistentList<T> by mutableStateOf(persistentListOf())
        private set

    var isInSelectionMode: Boolean by mutableStateOf(false)
        private set

    var selectedIds: Set<Any> by mutableStateOf(emptySet())
        private set

    /** Items with their `isSelected` flag derived from [selectedIds]. */
    val projectedItems: ImmutableList<T> by derivedStateOf {
        items.map { withSelection(it, idOf(it) in selectedIds) }.toImmutableList()
    }

    val selectedItems: ImmutableList<T> by derivedStateOf {
        projectedItems.filter { idOf(it) in selectedIds }.toImmutableList()
    }

    /** Subscribe the items source. Call from VM init. */
    fun bind(scope: CoroutineScope, itemsFlow: Flow<List<T>>) {
        itemsFlow
            .onEach { newItems -> items = sort(newItems).toPersistentList() }
            .launchIn(scope)
    }

    fun enterSelectionMode() {
        isInSelectionMode = true
    }

    fun exitSelectionMode() {
        isInSelectionMode = false
        selectedIds = emptySet()
    }

    fun toggleSelection(itemId: Any) {
        selectedIds = if (itemId in selectedIds) selectedIds - itemId else selectedIds + itemId
        if (selectedIds.isEmpty() && isInSelectionMode) {
            isInSelectionMode = false
        }
    }

    fun selectAll() {
        selectedIds = items.map(idOf).toSet()
    }
}
