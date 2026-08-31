package dev.thomas_kiljanczyk.lyriccast.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import java.util.UUID

@Entity(tableName = "setlists")
data class SetlistEntity(
    @PrimaryKey
    val id: UUID,
    val name: String
) {
    constructor(setlist: Setlist) : this(
        id = setlist.id,
        name = setlist.name
    )
}
