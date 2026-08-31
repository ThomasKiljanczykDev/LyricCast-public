package dev.thomas_kiljanczyk.lyriccast.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.core.database.LyricCastDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLyricCastDatabase(@ApplicationContext context: Context): LyricCastDatabase {
        return Room.databaseBuilder(
            context,
            LyricCastDatabase::class.java,
            "lyriccast_database"
        ).build()
    }
}
