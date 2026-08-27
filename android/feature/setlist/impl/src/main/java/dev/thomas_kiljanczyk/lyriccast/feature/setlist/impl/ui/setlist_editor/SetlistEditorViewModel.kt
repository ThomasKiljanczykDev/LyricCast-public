/*
 * Created by Tomasz Kiljanczyk on 9/12/25, 12:35 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/12/25, 12:26 AM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_editor

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.GetSetlistNamesUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.GetSongsByIdsUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.SaveSetlistUseCase
import dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor.ValidateSetlistNameUseCase
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.SaveSetlistResult
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.NameValidationState
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.R
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.domain.LoadSetlistUseCase
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.LoadSetlistResult
import dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model.SetlistSongItem
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface SetlistEditorState {
    val setlistName: String
    val songs: List<SetlistSongItem>
    val selectedSongs: List<SetlistSongItem>
    val isInSelectionMode: Boolean
    val nameValidationState: NameValidationState
    val canSave: Boolean
}

class MutableSetlistEditorState : SetlistEditorState {
    override var setlistName by mutableStateOf("")
    override var songs by mutableStateOf<List<SetlistSongItem>>(emptyList())
    override val selectedSongs by derivedStateOf {
        songs.filter { it.isSelected }
    }
    override var isInSelectionMode by mutableStateOf(false)
    override var nameValidationState by mutableStateOf(NameValidationState.VALID)
    override val canSave by derivedStateOf {
        nameValidationState == NameValidationState.VALID && setlistName.isNotBlank() && songs.isNotEmpty()
    }
}

@HiltViewModel
class SetlistEditorViewModel @Inject constructor(
    getSetlistNamesUseCase: GetSetlistNamesUseCase,
    private val validateSetlistNameUseCase: ValidateSetlistNameUseCase,
    private val saveSetlistUseCase: SaveSetlistUseCase,
    private val loadSetlistUseCase: LoadSetlistUseCase,
    private val getSongsByIdsUseCase: GetSongsByIdsUseCase
) : ViewModel() {

    private val _state = MutableSetlistEditorState()
    val state: SetlistEditorState get() = _state

    private var setlistId: UUID = UUIDv7.randomUUID()
    private var editedSetlist: Setlist? = null
    private var setlistNames: Set<String> = setOf()

    init {
        getSetlistNamesUseCase()
            .onEach { names -> setlistNames = names }
            .flowOn(Dispatchers.Default).launchIn(viewModelScope)
    }

    fun getSetlistSongIds(): List<UUID> {
        return _state.songs.map { it.song.id }
    }

    suspend fun loadSetlist(setlistId: UUID, presentation: List<UUID>) {
        when (val result = loadSetlistUseCase(setlistId)) {
            is LoadSetlistResult.Success -> {
                this.setlistId = setlistId
                _state.setlistName = result.setlist.name

                // Use custom presentation order if provided, otherwise use setlist's presentation
                if (presentation.isNotEmpty()) {
                    updatePresentation(presentation)
                } else {
                    _state.songs = result.setlistSongItems
                }
            }

            is LoadSetlistResult.Error -> {
                Log.e(
                    "SetlistEditorViewModel",
                    "Failed to load setlist for view: ${result.message}"
                )
                throw IllegalArgumentException("Setlist not found")
            }
        }
    }

    suspend fun loadAdhocSetlist(presentation: List<UUID>) {
        updatePresentation(presentation)
    }

    suspend fun updatePresentation(presentation: List<UUID>) {
        val songs = getSongsByIdsUseCase(presentation)
        _state.songs = songs.map { song ->
            SetlistSongItem(SongItem.fromSong(song))
        }
    }

    suspend fun loadEditedSetlist(setlistId: UUID) {
        when (val result = loadSetlistUseCase(setlistId)) {
            is LoadSetlistResult.Success -> {
                this.setlistId = setlistId
                editedSetlist = result.setlist
                _state.songs = result.setlistSongItems
                _state.setlistName = result.setlist.name
            }

            is LoadSetlistResult.Error -> {
                Log.e(
                    "SetlistEditorViewModel",
                    "Failed to load setlist for editing: ${result.message}"
                )
                editedSetlist = null
            }
        }
    }

    fun setSetlistName(name: String) {
        _state.setlistName = name
        _state.nameValidationState = validateSetlistName(name)
    }

    fun toggleSelectionMode() {
        _state.isInSelectionMode = !_state.isInSelectionMode

        if (!_state.isInSelectionMode) {
            // Clear selection when exiting selection mode
            _state.songs = _state.songs.map { it.copy(isSelected = false) }
        }
    }

    fun selectSong(songId: UUID, selected: Boolean) {
        _state.songs = _state.songs.map { song ->
            if (song.id == songId) {
                song.copy(isSelected = selected)
            } else {
                song
            }
        }

        // Exit selection mode if no songs are selected
        if (_state.isInSelectionMode && _state.songs.none { it.isSelected }) {
            _state.isInSelectionMode = false
        }
    }

    fun clearSelection() {
        _state.songs = _state.songs.map { it.copy(isSelected = false) }
        _state.isInSelectionMode = false
    }

    fun moveSong(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return

        val currentSongs = _state.songs.toMutableList()
        currentSongs.add(toIndex, currentSongs.removeAt(fromIndex))
        _state.songs = currentSongs
    }

    fun removeSongAt(index: Int) {
        _state.songs = _state.songs.filterIndexed { i, _ -> i != index }
    }

    fun removeSelectedSongs() {
        _state.songs = _state.songs.filter { !it.isSelected }
        _state.isInSelectionMode = false
    }

    fun duplicateSelectedSongs() {
        val songsAfterDuplicate = mutableListOf<SetlistSongItem>()

        _state.songs.forEach { song ->
            songsAfterDuplicate.add(song)
            if (song.isSelected) {
                songsAfterDuplicate.add(
                    song.copy(
                        id = UUIDv7.randomUUID(), isSelected = false
                    )
                )
            }
        }

        _state.songs = songsAfterDuplicate
        clearSelection()
    }

    fun duplicateSongAt(index: Int) {
        if (index !in _state.songs.indices) return

        val songToDuplicate = _state.songs[index]
        val duplicatedSong = songToDuplicate.copy(
            id = UUIDv7.randomUUID(),
            isSelected = false
        )

        val currentSongs = _state.songs.toMutableList()
        currentSongs.add(index + 1, duplicatedSong)
        _state.songs = currentSongs
    }

    suspend fun saveSetlist(): SaveSetlistResult {
        if (!state.canSave) {
            return SaveSetlistResult.ValidationError(
                UiText.StringResource(R.string.setlist_editor_validation_failed)
            )
        }

        val presentation = _state.songs.map { setlistSongItem ->
            Song(
                id = setlistSongItem.song.id,
                title = setlistSongItem.song.title,
                lyrics = setlistSongItem.song.lyricsMap.map { (key, value) ->
                    Song.LyricsSection(key, value)
                },
                presentation = setlistSongItem.song.presentation.toList(),
                category = setlistSongItem.song.category?.let { categoryItem ->
                    Category(
                        id = categoryItem.id,
                        name = categoryItem.name,
                        color = categoryItem.color
                    )
                }
            )
        }
        return saveSetlistUseCase(
            setlistId = setlistId,
            name = _state.setlistName,
            songs = presentation,
            existingNames = setlistNames,
            currentName = editedSetlist?.name
        )
    }

    private fun validateSetlistName(name: String): NameValidationState {
        return validateSetlistNameUseCase(
            setlistName = name,
            existingNames = setlistNames,
            currentName = editedSetlist?.name
        )
    }

    fun onBackPressed(): Boolean {
        return if (_state.isInSelectionMode) {
            toggleSelectionMode()
            true
        } else {
            false
        }
    }

    suspend fun updateSongsFromSelection(selectedSongIds: List<UUID>) {
        val selectedSongIdsSet = selectedSongIds.toSet()
        val currentSongIdsSet = getSetlistSongIds().toSet()
        val removedSongIds = currentSongIdsSet.filter { it !in selectedSongIdsSet }
        val addedSongIds = selectedSongIdsSet.filter { it !in currentSongIdsSet }

        val remainingSongs = _state.songs.filter { it.song.id !in removedSongIds }
        val addedSongs = getSongsByIdsUseCase(addedSongIds).map { song ->
            SetlistSongItem(SongItem.fromSong(song), UUIDv7.randomUUID())
        }

        _state.songs = remainingSongs + addedSongs
    }
}
