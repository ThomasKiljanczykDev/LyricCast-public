/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 1:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.thomas_kiljanczyk.lyriccast.datamodel.dao.CategoryDao
import dev.thomas_kiljanczyk.lyriccast.datamodel.dao.SetlistDao
import dev.thomas_kiljanczyk.lyriccast.datamodel.dao.SongDao
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.CategoryEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.Converters
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.LyricsSectionEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SetlistSongCrossRef
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.room.SongEntity

@Database(
    entities = [
        CategoryEntity::class,
        SongEntity::class,
        LyricsSectionEntity::class,
        SetlistEntity::class,
        SetlistSongCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun songDao(): SongDao
    abstract fun setlistDao(): SetlistDao
}
