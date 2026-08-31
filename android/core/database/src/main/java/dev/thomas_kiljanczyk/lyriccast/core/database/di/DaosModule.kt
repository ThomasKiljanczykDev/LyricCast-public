package dev.thomas_kiljanczyk.lyriccast.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.core.database.LyricCastDatabase
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.CategoryDao
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SetlistDao
import dev.thomas_kiljanczyk.lyriccast.core.database.dao.SongDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    @Singleton
    fun provideCategoryDao(database: LyricCastDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideSongDao(database: LyricCastDatabase): SongDao = database.songDao()

    @Provides
    @Singleton
    fun provideSetlistDao(database: LyricCastDatabase): SetlistDao = database.setlistDao()
}
