/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 9:14 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 9:13 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.models.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "setlist_songs",
    primaryKeys = ["setlistId", "songId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = SetlistEntity::class,
            parentColumns = ["id"],
            childColumns = ["setlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["setlistId"]),
        Index(value = ["songId"])
    ]
)
data class SetlistSongCrossRef(
    val setlistId: UUID,
    val songId: UUID,
    val position: Int
)
