package dev.thomas_kiljanczyk.lyriccast.core.ui.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.thomas_kiljanczyk.lyriccast.common.extensions.normalize
import dev.thomas_kiljanczyk.lyriccast.core.model.CategoryItem
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import java.util.UUID

interface SongFilterState {
    val searchText: String
    val selectedCategory: CategoryItem?
    val selectedCategoryId: UUID?
    val showOnlySelected: Boolean
    val normalizedSearchText: String

    fun filterSongs(songs: List<SongItem>): List<SongItem>
}

class MutableSongFilterState : SongFilterState {
    override var searchText by mutableStateOf("")
    override var selectedCategory by mutableStateOf<CategoryItem?>(null)
    override val selectedCategoryId by derivedStateOf { selectedCategory?.id }
    override var showOnlySelected by mutableStateOf(false)
    override val normalizedSearchText by derivedStateOf { searchText.normalize() }

    override fun filterSongs(songs: List<SongItem>): List<SongItem> {
        return songs.filter { songItem ->
            if (showOnlySelected && !songItem.isSelected) {
                return@filter false
            }

            if (selectedCategoryId != null && songItem.category?.id != selectedCategoryId) {
                return@filter false
            }

            if (normalizedSearchText.isNotBlank() && !songItem.normalizedTitle.contains(
                    normalizedSearchText, true
                )
            ) {
                return@filter false
            }

            true
        }
    }
}
