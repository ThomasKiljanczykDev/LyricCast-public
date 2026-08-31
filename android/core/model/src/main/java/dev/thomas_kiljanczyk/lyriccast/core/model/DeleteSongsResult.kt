package dev.thomas_kiljanczyk.lyriccast.core.model

import java.util.UUID

sealed class DeleteSongsResult {
    data class Success(val deletedCount: Int) : DeleteSongsResult()
    data class SongsInUse(val songIds: List<UUID>) : DeleteSongsResult()
    data class Error(val message: UiText) : DeleteSongsResult()
}
