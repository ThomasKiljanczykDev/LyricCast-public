/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.song_controls

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
import dev.thomas_kiljanczyk.lyriccast.application.AppSettings
import dev.thomas_kiljanczyk.lyriccast.application.CastConfiguration
import dev.thomas_kiljanczyk.lyriccast.application.getCastConfiguration
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastSessionListener
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.ReceivedPayload
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.ShowLyricsContent
import dev.thomas_kiljanczyk.lyriccast.shared.misc.LyricCastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionServerCommand
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SessionServerMessage
import dev.thomas_kiljanczyk.lyriccast.ui.shared.misc.settings.ControlButtonHeightOption
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface SongControlsState {
    val songTitle: String
    val currentSlideText: String
    val currentSlide: Int
    val totalSlideCount: Int
    val isBlanked: Boolean
    val buttonHeight: Int
}

class MutableSongControlsState : SongControlsState {
    override var songTitle by mutableStateOf("")
    override var currentSlideText by mutableStateOf("")
    override var currentSlide by mutableIntStateOf(0)
    override var totalSlideCount by mutableIntStateOf(0)
    override var isBlanked by mutableStateOf(true)
    override var buttonHeight by mutableIntStateOf(ControlButtonHeightOption.DEFAULT.value)
}

@HiltViewModel
class SongControlsViewModel @Inject constructor(
    dataStore: DataStore<AppSettings>,
    private val songsRepository: SongsRepository,
    private val castMessagingContext: CastMessagingContext,
    private val lyricCastMessagingContext: LyricCastMessagingContext,
    private val castContext: CastContext
) : ViewModel() {
    companion object {
        private const val TAG = "SongControlsModel"
    }

    private var castConfiguration: CastConfiguration? = null
    private lateinit var lyrics: List<String>

    private val _state = MutableSongControlsState()
    val state: SongControlsState get() = _state

    private val castSessionListener: CastSessionListener = CastSessionListener(onStarted = {
        viewModelScope.launch {
            if (castConfiguration != null) sendConfiguration()
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

    private fun handlePayload(receivedPayload: ReceivedPayload) {
        val content = SessionServerMessage.fromJson(receivedPayload.payload) ?: return

        when (content.command) {
            SessionServerCommand.SEND_LATEST_SLIDE -> {
                lyricCastMessagingContext.sendContentMessage(
                    receivedPayload.endpointId, getCurrentShowLyricsContent()
                )
            }
        }
    }

    suspend fun loadSong(songId: UUID) {
        val song: Song = songsRepository.getSong(songId)!!

        lyrics = song.lyricsList

        _state.apply {
            songTitle = song.title
            currentSlideText = if (lyrics.isNotEmpty()) lyrics[0] else ""
            currentSlide = 0
            totalSlideCount = lyrics.size
            isBlanked = castMessagingContext.isBlanked.value
            buttonHeight = ControlButtonHeightOption.DEFAULT.value
        }
    }

    suspend fun goToPreviousSlide() {
        if (_state.currentSlide <= 0) {
            return
        }
        val newSlide = _state.currentSlide - 1
        _state.apply {
            currentSlide = newSlide
            currentSlideText = lyrics[newSlide]
        }
        sendSlide()
    }

    suspend fun goToNextSlide() {
        if (_state.currentSlide >= lyrics.size - 1) {
            return
        }
        val newSlide = _state.currentSlide + 1
        _state.apply {
            currentSlide = newSlide
            currentSlideText = lyrics[newSlide]
        }

        sendSlide()
    }

    suspend fun sendBlank() {
        castMessagingContext.sendBlank(!castMessagingContext.isBlanked.value)
    }

    private suspend fun sendConfiguration() {
        castMessagingContext.sendConfiguration(castConfiguration!!)
    }

    suspend fun sendSlide() {
        lyricCastMessagingContext.broadcastContentMessage(
            getCurrentShowLyricsContent()
        )
    }

    private fun getCurrentShowLyricsContent(): ShowLyricsContent {
        return ShowLyricsContent(
            state.songTitle,
            state.currentSlideText,
            state.currentSlide,
            state.totalSlideCount
        )
    }
}
