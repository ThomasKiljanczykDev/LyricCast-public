package dev.thomas_kiljanczyk.lyriccast.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
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
