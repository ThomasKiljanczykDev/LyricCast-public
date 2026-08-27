/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import dev.thomas_kiljanczyk.lyriccast.core.model.Song

data class SongWithLyricsAndCategory(
    @Embedded val song: SongEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "songId"
    )
    val lyricsSections: List<LyricsSectionEntity>,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
) {
    fun toGenericModel(): Song {
        val sortedLyrics = lyricsSections.sortedBy { it.orderIndex }
        return Song(
            id = song.id,
            title = song.title,
            lyrics = sortedLyrics.map { it.toGenericModel() },
            presentation = song.presentation,
            category = category?.toGenericModel()
        )
    }
}
