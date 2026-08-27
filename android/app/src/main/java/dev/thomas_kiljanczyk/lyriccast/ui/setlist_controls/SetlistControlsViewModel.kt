/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:44 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.setlist_controls

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.application.CastConfiguration
import dev.thomas_kiljanczyk.lyriccast.application.getCastConfiguration
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastSessionListener
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.ReceivedPayload
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.ShowLyricsContent
import dev.thomas_kiljanczyk.lyriccast.shared.misc.LyricCastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionServerCommand
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionServerMessage
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface SetlistControlsState {
    val songs: List<SongItem>
    val currentSlideText: String
    val currentSongTitle: String
    val currentSlide: Int
    val totalSlideCount: Int
    val currentSongPosition: Int
    val isBlanked: Boolean
    val buttonHeight: Int
}

class MutableSetlistControlsState : SetlistControlsState {
    override var songs by mutableStateOf<List<SongItem>>(listOf())
    override var currentSlideText by mutableStateOf("")
    override var currentSongTitle by mutableStateOf("")
    override var currentSlide by mutableIntStateOf(0)
    override var totalSlideCount by mutableIntStateOf(0)
    override var currentSongPosition by mutableIntStateOf(0)
    override var isBlanked by mutableStateOf(true)
    override var buttonHeight by mutableIntStateOf(ControlButtonHeightOption.DEFAULT.value)
}

@HiltViewModel
class SetlistControlsViewModel @Inject constructor(
    dataStore: DataStore<AppSettings>,
    private val castContext: CastContext,
    private val setlistsRepository: SetlistsRepository,
    private val castMessagingContext: CastMessagingContext,
    private val lyricCastMessagingContext: LyricCastMessagingContext
) : ViewModel() {
    companion object {
        private const val TAG = "SetlistControlsModel"
    }

    private var castConfiguration: CastConfiguration? = null

    private val _state = MutableSetlistControlsState()
    val state: SetlistControlsState get() = _state

    // Simplified state - single source of truth
    private var songs: List<SongItem> = emptyList()
    private var currentSongIndex: Int = 0
    private var currentSlideIndex: Int = 0

    private val currentSong
        get() = songs.getOrNull(currentSongIndex)?.let { songItem ->
            Song(
                id = songItem.id,
                title = songItem.title,
                lyrics = songItem.lyricsMap.map { (key, value) ->
                    Song.LyricsSection(key, value)
                },
                presentation = songItem.presentation,
                category = songItem.category?.let { categoryItem ->
                    Category(
                        id = categoryItem.id,
                        name = categoryItem.name,
                        color = categoryItem.color
                    )
                }
            )
        }
    private val currentLyrics get() = currentSong?.lyricsList ?: emptyList()

    private val castSessionListener: CastSessionListener = CastSessionListener(onStarted = {
        viewModelScope.launch {
            if (castConfiguration != null) {
                sendConfiguration()
            }
            sendSlide()
        }
    })

    init {
        dataStore.data.onEach { settings ->
            castConfiguration = settings.getCastConfiguration()
            sendConfiguration()

            // Update button height from settings
            val height = if (settings.controlButtonsHeight > 0.0f) {
                settings.controlButtonsHeight
            } else {
                ControlButtonHeightOption.DEFAULT.value
            }

            _state.buttonHeight = height
        }.flowOn(Dispatchers.Default).launchIn(viewModelScope)

        castMessagingContext.isBlanked.onEach { blanked ->
            _state.isBlanked = blanked
        }.flowOn(Dispatchers.Default).launchIn(viewModelScope)

        lyricCastMessagingContext.receivedPayload.onEach {
            Log.d(TAG, "Received payload: $it")
        }.onEach(::handlePayload).flowOn(Dispatchers.Default).launchIn(viewModelScope)

        viewModelScope.launch(Dispatchers.Main) {
            castContext.sessionManager.addSessionManagerListener(castSessionListener)
        }
    }

    override fun onCleared() {
        // Must happen outside of the ViewModel scope
        CoroutineScope(Dispatchers.Main).launch {
            castContext.sessionManager.removeSessionManagerListener(castSessionListener)
        }

        super.onCleared()
    }

    suspend fun loadSetlist(setlistId: UUID) {
        val setlist = setlistsRepository.getSetlist(setlistId)!!
        songs = setlist.presentation.map { SongItem.fromSong(it, false) }
        currentSongIndex = 0
        currentSlideIndex = 0

        _state.apply {
            songs = this@SetlistControlsViewModel.songs
            currentSongPosition = currentSongIndex
        }

        updateCurrentSlide()
    }

    fun goToPreviousSlide() {
        when {
            currentSlideIndex > 0 -> {
                currentSlideIndex--
                updateCurrentSlide()
            }

            currentSongIndex > 0 -> {
                currentSongIndex--
                currentSlideIndex = (currentLyrics.size - 1).coerceAtLeast(0)
                updateCurrentSlide()
            }
        }
    }

    fun goToNextSlide() {
        when {
            currentSlideIndex < currentLyrics.size - 1 -> {
                currentSlideIndex++
                updateCurrentSlide()
            }

            currentSongIndex < songs.size - 1 -> {
                currentSongIndex++
                currentSlideIndex = 0
                updateCurrentSlide()
            }
        }
    }

    suspend fun sendBlank() {
        castMessagingContext.sendBlank(!castMessagingContext.isBlanked.value)
    }

    private suspend fun sendConfiguration() {
        castMessagingContext.sendConfiguration(castConfiguration!!)
    }

    private fun handlePayload(receivedPayload: ReceivedPayload) {
        val content = SessionServerMessage.fromJson(receivedPayload.payload) ?: return

        when (content.command) {
            SessionServerCommand.SEND_LATEST_SLIDE -> {
                val song = currentSong ?: return
                val slideText = currentLyrics.getOrNull(currentSlideIndex) ?: ""
                val showLyricsContent = ShowLyricsContent(
                    song.title, slideText, currentSlideIndex, currentLyrics.size
                )
                lyricCastMessagingContext.sendContentMessage(
                    receivedPayload.endpointId, showLyricsContent
                )
            }
        }
    }

    fun selectSong(position: Int, fromStart: Boolean = false) {
        if (position in songs.indices) {
            currentSongIndex = position
            currentSlideIndex =
                if (fromStart) 0 else currentSlideIndex.coerceAtMost(currentLyrics.size - 1)
            updateCurrentSlide()
        }
    }

    private fun updateCurrentSlide() = viewModelScope.launch(Dispatchers.Default) {
        val song = currentSong ?: return@launch
        val slideText = currentLyrics.getOrNull(currentSlideIndex) ?: ""

        val showLyricsContent = ShowLyricsContent(
            song.title, slideText, currentSlideIndex, currentLyrics.size
        )

        lyricCastMessagingContext.broadcastContentMessage(showLyricsContent)

        _state.apply {
            currentSlideText = slideText
            currentSongTitle = song.title
            currentSlide = currentSlideIndex
            totalSlideCount = currentLyrics.size
            currentSongPosition = currentSongIndex
            songs = this@SetlistControlsViewModel.songs
        }
    }

    fun sendSlide() {
        updateCurrentSlide()
    }
}
