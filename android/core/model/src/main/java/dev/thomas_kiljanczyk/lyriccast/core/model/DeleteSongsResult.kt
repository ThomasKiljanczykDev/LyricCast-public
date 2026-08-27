/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:23 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.model

import java.util.UUID

/**
 * Result of deleting songs operation.
 */
sealed class DeleteSongsResult {
    data class Success(val deletedCount: Int) : DeleteSongsResult()
    data class SongsInUse(val songIds: List<UUID>) : DeleteSongsResult()
    data class Error(val message: UiText) : DeleteSongsResult()
}
