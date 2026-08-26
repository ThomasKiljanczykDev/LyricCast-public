/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.Setlist
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
