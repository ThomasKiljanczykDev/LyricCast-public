/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import java.util.UUID

@Entity(
    tableName = "songs",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class SongEntity(
    @PrimaryKey
    val id: UUID,
    val title: String,
    val categoryId: UUID?,
    val presentation: List<String>
) {
    constructor(song: Song) : this(
        id = song.id,
        title = song.title,
        categoryId = song.category?.id,
        presentation = song.presentation
    )
}
