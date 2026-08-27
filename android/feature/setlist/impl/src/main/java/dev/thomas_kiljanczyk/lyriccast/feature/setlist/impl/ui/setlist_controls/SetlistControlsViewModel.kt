/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 6:08 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/8/25, 2:44 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.ui.setlist_controls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.cast.framework.CastContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackController
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackState
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class SetlistControlsViewModel @Inject constructor(
    private val castContext: CastContext?,
    private val setlistsRepository: SetlistsRepository,
    private val playbackController: PlaybackController
) : ViewModel() {

    val state: StateFlow<PlaybackState> get() = playbackController.state

    init {
        playbackController.bind(viewModelScope, castContext)
    }

    override fun onCleared() {
        playbackController.unbind(castContext)
        super.onCleared()
    }

    suspend fun loadSetlist(setlistId: UUID) {
        val setlist = setlistsRepository.getSetlist(setlistId)!!
        playbackController.loadSetlist(setlist)
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

    suspend fun selectSong(position: Int, fromStart: Boolean = false) {
        playbackController.selectSong(position, fromStart)
    }

    suspend fun sendSlide() {
        playbackController.sendCurrentSlide()
    }
}
