/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 3:45 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 2:05 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.sync

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DatabaseTransferData
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SetlistDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import kotlinx.serialization.Serializable

/**
 * Message protocol for GMS Nearby data synchronization.
 *
 * Carries the same DTOs as [DatabaseTransferData] flattened onto the message itself (rather
 * than nesting that type directly) so `core:sync` doesn't have to make `core:data` depend on
 * kotlinx-serialization just for this one wire type.
 *
 * Version: 1.0 - Initial implementation
 */
@Serializable
data class GmsSyncMessage(
    val version: Int = CURRENT_VERSION,
    val messageType: SyncMessageType,
    val senderDeviceName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val songDtos: List<SongDto>?,
    val categoryDtos: List<CategoryDto>?,
    val setlistDtos: List<SetlistDto>?
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}

@Serializable
enum class SyncMessageType {
    SYNC_DATA
}

fun DatabaseTransferData.toSyncMessage(senderDeviceName: String): GmsSyncMessage = GmsSyncMessage(
    messageType = SyncMessageType.SYNC_DATA,
    senderDeviceName = senderDeviceName,
    songDtos = songDtos,
    categoryDtos = categoryDtos,
    setlistDtos = setlistDtos
)

fun GmsSyncMessage.toTransferData(): DatabaseTransferData = DatabaseTransferData(
    songDtos = songDtos,
    categoryDtos = categoryDtos,
    setlistDtos = setlistDtos
)
