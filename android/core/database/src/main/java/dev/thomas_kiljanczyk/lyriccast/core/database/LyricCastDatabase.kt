package dev.thomas_kiljanczyk.lyriccast.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.CategoryDao
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SetlistDao
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SongDao
import dev.thomas_kiljanczyk.lyriccast.core.database.model.CategoryEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.Converters
import dev.thomas_kiljanczyk.lyriccast.core.database.model.LyricsSectionEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SetlistEntity
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SetlistSongCrossRef
import dev.thomas_kiljanczyk.lyriccast.core.database.model.SongEntity

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
abstract class LyricCastDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun songDao(): SongDao
    abstract fun setlistDao(): SetlistDao
}
