/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.models

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import java.util.UUID

data class Song(
    var id: UUID = UUIDv7.randomUUID(),
    var title: String,
    var lyrics: List<LyricsSection>,
    var presentation: List<String>,
    var category: Category? = null
) {

    data class LyricsSection(
        var name: String,
        var text: String
    )

    val lyricsMap: Map<String, String> = lyrics.associate { it.name to it.text }

    val lyricsList: List<String> = presentation.map { lyricsMap[it]!! }

    constructor() : this(
        id = UUIDv7.randomUUID(),
        title = "", lyrics = listOf(),
        presentation = listOf(),
        category = null
    )

    internal constructor(dto: SongDto, category: Category?) : this(
        id = UUIDv7.randomUUID(),
        title = dto.title,
        lyrics = listOf(),
        presentation = listOf(),
        category = category
    )

    fun toDto(): SongDto {
        return SongDto(title, lyricsMap, presentation.toList(), category?.name ?: "")
    }
}

