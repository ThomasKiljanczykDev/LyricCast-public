
package dev.thomas_kiljanczyk.lyriccast.feature.setlist.impl.model

import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText

sealed class LoadSetlistResult {
    data class Success(
        val setlist: Setlist,
        val setlistSongItems: List<SetlistSongItem>
    ) : LoadSetlistResult()

    data class Error(val message: UiText) : LoadSetlistResult()
}
