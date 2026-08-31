package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SetlistDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto

data class DatabaseTransferData(
    val songDtos: List<SongDto>?,
    val categoryDtos: List<CategoryDto>?,
    val setlistDtos: List<SetlistDto>?
)
