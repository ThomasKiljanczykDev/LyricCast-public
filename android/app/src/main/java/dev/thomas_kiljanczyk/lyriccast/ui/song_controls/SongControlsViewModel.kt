/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.ui.song_controls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackController
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackState
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SongControlsViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val playbackController: PlaybackController,
    private val castContext: CastContext
) : ViewModel() {

    val state: StateFlow<PlaybackState> get() = playbackController.state

    init {
        playbackController.bind(viewModelScope, castContext)
    }

    override fun onCleared() {
        playbackController.unbind(castContext)
        super.onCleared()
    }

    suspend fun loadSong(songId: UUID) {
        val song = songsRepository.getSong(songId)!!
        playbackController.loadSong(song)
    }

    suspend fun goToPreviousSlide() {
        playbackController.goToPreviousSlide()
    }

    suspend fun goToNextSlide() {
        playbackController.goToNextSlide()
    }

    suspend fun sendBlank() {
        playbackController.setBlank()
    }

    suspend fun sendSlide() {
        playbackController.sendCurrentSlide()
    }
}
