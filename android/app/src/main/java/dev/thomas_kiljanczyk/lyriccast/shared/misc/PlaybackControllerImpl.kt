/*
 * Created by Tomasz Kiljanczyk on 5/31/26, 1:21 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/31/26, 12:43 PM
 */

package dev.thomas_kiljanczyk.lyriccast.shared.misc

import androidx.datastore.core.DataStore
import com.google.android.gms.cast.framework.CastContext
import dev.thomas_kiljanczyk.lyriccast.core.cast.CastConfiguration
import dev.thomas_kiljanczyk.lyriccast.core.cast.CastSessionListener
import dev.thomas_kiljanczyk.lyriccast.core.cast.SlidePresentationBus
import dev.thomas_kiljanczyk.lyriccast.core.cast.getCastConfiguration
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackController
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackState
import dev.thomas_kiljanczyk.lyriccast.core.session.ReceivedPayload
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionServerCommand
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionServerMessage
import dev.thomas_kiljanczyk.lyriccast.core.session.SetlistContent
import dev.thomas_kiljanczyk.lyriccast.core.session.SetlistSongContent
import dev.thomas_kiljanczyk.lyriccast.core.session.ShowLyricsContent
import dev.thomas_kiljanczyk.lyriccast.core.session.decodeOrNull
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Drives Song/Setlist slide navigation and fans updates out through [SlidePresentationBus].
 * Used to live inline in `SongControlsViewModel`/`SetlistControlsViewModel`; both now delegate
 * here so the Cast session lifecycle and the session-server SEND_LATEST_SLIDE handling live in
 * one place.
 */
class PlaybackControllerImpl(
    private val dataStore: DataStore<AppSettings>,
    private val bus: SlidePresentationBus,
    private val payloadTransport: PayloadTransport,
    private val codec: SessionMessageCodec,
    private val mainDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher
) : PlaybackController {

    private sealed interface Mode {
        data class SingleSong(val song: Song) : Mode
        data class SetlistMode(val songs: List<Song>) : Mode
    }

    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var mode: Mode? = null
    private var songIndex: Int = 0
    private var slideIndex: Int = 0

    private var castConfiguration: CastConfiguration? = null
    private var castSessionListener: CastSessionListener? = null

    private val currentSong: Song?
        get() = when (val m = mode) {
            is Mode.SingleSong -> m.song
            is Mode.SetlistMode -> m.songs.getOrNull(songIndex)
            null -> null
        }

    private val currentLyrics: List<String>
        get() = currentSong?.lyricsList ?: emptyList()

    /** Null in single-song mode. */
    private val currentSetlistContent: SetlistContent?
        get() = when (val m = mode) {
            is Mode.SetlistMode -> SetlistContent(
                songs = m.songs.map { SetlistSongContent(it.id.toString(), it.title) },
                currentSongIndex = songIndex
            )

            is Mode.SingleSong, null -> null
        }

    private fun currentSlideContent(song: Song, lyrics: List<String>) = ShowLyricsContent(
        songTitle = song.title,
        slideText = lyrics.getOrNull(slideIndex) ?: "",
        slideNumber = slideIndex,
        totalSlides = lyrics.size,
        setlist = currentSetlistContent
    )

    override fun bind(scope: CoroutineScope, castContext: CastContext?) {
        dataStore.data.onEach { settings ->
            val configuration = settings.getCastConfiguration()
            castConfiguration = configuration
            bus.sendConfiguration(configuration)

            val height = if (settings.controlButtonsHeight > 0) {
                settings.controlButtonsHeight
            } else {
                ControlButtonHeightOption.DEFAULT.value
            }
            _state.update { it.copy(buttonHeight = height) }
        }.launchIn(scope)

        bus.isBlanked.onEach { blanked ->
            _state.update { it.copy(isBlanked = blanked) }
        }.launchIn(scope)

        bus.receivedPayload.onEach(::handlePayload).launchIn(scope)

        payloadTransport.serverIsRunning.onEach { running ->
            _state.update { it.copy(isSessionRunning = running) }
        }.launchIn(scope)

        if (castContext != null) {
            val listener = CastSessionListener(onStarted = {
                scope.launch {
                    castConfiguration?.let { bus.sendConfiguration(it) }
                    presentCurrent()
                }
            })
            castSessionListener = listener
            scope.launch(mainDispatcher) {
                castContext.sessionManager.addSessionManagerListener(listener)
            }
        }
    }

    override fun unbind(castContext: CastContext?) {
        val listener = castSessionListener ?: return
        castSessionListener = null
        castContext?.sessionManager?.removeSessionManagerListener(listener)
    }

    override suspend fun loadSong(song: Song) {
        mode = Mode.SingleSong(song)
        songIndex = 0
        slideIndex = 0

        _state.update {
            it.copy(
                songs = emptyList(),
                currentSongPosition = 0
            )
        }

        presentCurrent()
    }

    override suspend fun loadSetlist(setlist: Setlist) {
        val songs = setlist.presentation
        mode = Mode.SetlistMode(songs)
        songIndex = 0
        slideIndex = 0

        val songItems = withContext(defaultDispatcher) {
            songs.map { SongItem.fromSong(it, false) }
        }
        _state.update {
            it.copy(
                songs = songItems,
                currentSongPosition = 0
            )
        }

        presentCurrent()
    }

    override suspend fun goToPreviousSlide() {
        val lyrics = currentLyrics
        if (lyrics.isEmpty()) return

        when {
            slideIndex > 0 -> {
                slideIndex--
                presentCurrent()
            }

            mode is Mode.SetlistMode && songIndex > 0 -> {
                songIndex--
                slideIndex = (currentLyrics.size - 1).coerceAtLeast(0)
                presentCurrent()
            }
        }
    }

    override suspend fun goToNextSlide() {
        val lyrics = currentLyrics
        if (lyrics.isEmpty()) return

        when {
            slideIndex < lyrics.size - 1 -> {
                slideIndex++
                presentCurrent()
            }

            mode is Mode.SetlistMode -> {
                val setlistSongs = (mode as Mode.SetlistMode).songs
                if (songIndex < setlistSongs.size - 1) {
                    songIndex++
                    slideIndex = 0
                    presentCurrent()
                }
            }
        }
    }

    override suspend fun selectSong(position: Int, fromStart: Boolean) {
        val setlistMode = mode as? Mode.SetlistMode ?: return
        if (position !in setlistMode.songs.indices) return

        songIndex = position
        slideIndex = if (fromStart) {
            0
        } else {
            slideIndex.coerceAtMost((currentLyrics.size - 1).coerceAtLeast(0))
        }
        presentCurrent()
    }

    override suspend fun sendCurrentSlide() {
        presentCurrent()
    }

    override suspend fun setBlank() {
        bus.setBlank(!bus.isBlanked.value)
    }

    private suspend fun presentCurrent() {
        val song = currentSong ?: return
        val lyrics = currentLyrics
        val slideText = lyrics.getOrNull(slideIndex) ?: ""

        _state.update {
            it.copy(
                songTitle = song.title,
                currentSlideText = slideText,
                currentSlide = slideIndex,
                totalSlideCount = lyrics.size,
                currentSongPosition = songIndex
            )
        }

        bus.presentSlide(currentSlideContent(song, lyrics))
    }

    private suspend fun handlePayload(receivedPayload: ReceivedPayload) {
        val content = codec.decodeOrNull<SessionServerMessage>(receivedPayload.payload) ?: return
        when (content.command) {
            SessionServerCommand.SEND_LATEST_SLIDE -> {
                val song = currentSong ?: return
                bus.presentSlideTo(
                    receivedPayload.endpointId,
                    currentSlideContent(song, currentLyrics)
                )
            }
        }
    }
}
