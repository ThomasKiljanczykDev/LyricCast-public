/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:37 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.models.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Song
import java.util.UUID

@Entity(
    tableName = "lyrics_sections",
    foreignKeys = [
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["songId"])]
)
data class LyricsSectionEntity(
    @PrimaryKey
    val id: UUID = UUIDv7.randomUUID(),
    val songId: UUID,
    val name: String,
    val text: String,
    val orderIndex: Int
) {
    fun toGenericModel(): Song.LyricsSection {
        return Song.LyricsSection(
            name = name,
            text = text
        )
    }
}
