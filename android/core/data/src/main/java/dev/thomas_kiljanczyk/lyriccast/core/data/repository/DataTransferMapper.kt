package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SetlistDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto

/**
 * Converts between the transfer DTOs (serialized on export/import) and the domain models used by
 * the repositories. Kept separate from the domain models themselves so [core.model] does not need
 * to depend on [core.data-transfer].
 */
internal object DataTransferMapper {

    fun CategoryDto.toModel(): Category = Category(
        name = name,
        color = color,
        id = UUIDv7.randomUUID()
    )

    fun Category.toDto(): CategoryDto = CategoryDto(name, color)

    fun SongDto.toModel(category: Category?): Song = Song(
        id = UUIDv7.randomUUID(),
        title = title,
        lyrics = listOf(),
        presentation = listOf(),
        category = category
    )

    fun Song.toDto(): SongDto = SongDto(title, lyricsMap, presentation.toList(), category?.name ?: "")

    fun SetlistDto.toModel(): Setlist = Setlist(
        id = UUIDv7.randomUUID(),
        name = name,
        presentation = listOf()
    )

    fun Setlist.toDto(): SetlistDto {
        val songs = presentation.map { it.title }
        return SetlistDto(name, songs)
    }
}
