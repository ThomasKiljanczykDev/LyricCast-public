package dev.thomas_kiljanczyk.lyriccast.core.playback

import com.google.android.gms.cast.framework.CastContext
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.model.SongItem
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Unified state for both single-song and setlist playback.
 * Setlist-specific fields ([songs], [currentSongPosition]) stay default for single-song mode.
 */
data class PlaybackState(
    val songTitle: String = "",
    val currentSlideText: String = "",
    val currentSlide: Int = 0,
    val totalSlideCount: Int = 0,
    val isBlanked: Boolean = true,
    val buttonHeight: Int = ControlButtonHeightOption.DEFAULT.value,
    val isSessionRunning: Boolean = false,
    /** A Cast session is connected. Cast-only controls (blank) are inert without one. */
    val isCastConnected: Boolean = false,
    val songs: List<SongItem> = emptyList(),
    val currentSongPosition: Int = 0
)

/**
 * Drives slide/song navigation for a Song or Setlist playback context.
 *
 * Both Song and Setlist Controls VMs delegate here. The controller owns the cursor,
 * fans slide/blank/configuration updates through [dev.thomas_kiljanczyk.lyriccast.core.cast.SlidePresentationBus],
 * responds to SEND_LATEST_SLIDE requests, and subscribes to settings + session-server flows.
 */
interface PlaybackController {
    val state: StateFlow<PlaybackState>

    /** Call from ViewModel init. */
    fun bind(scope: CoroutineScope, castContext: CastContext?)

    /** Call from ViewModel onCleared. */
    fun unbind(castContext: CastContext?)

    suspend fun loadSong(song: Song)
    suspend fun loadSetlist(setlist: Setlist)

    suspend fun goToPreviousSlide()
    suspend fun goToNextSlide()
    suspend fun selectSong(position: Int, fromStart: Boolean = false)
    suspend fun sendCurrentSlide()

    suspend fun setBlank()
}
