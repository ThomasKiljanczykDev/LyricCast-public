/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:22 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.datamodel.RepositoryFactory
import dev.thomas_kiljanczyk.lyriccast.datamodel.database.AppDatabase
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModelModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lyriccast_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongsRepository(repositoryFactory: RepositoryFactory): SongsRepository {
        return repositoryFactory.createSongsRepository(RepositoryFactory.RepositoryProvider.ROOM)
    }

    @Provides
    @Singleton
    fun provideSetlistsRepository(repositoryFactory: RepositoryFactory): SetlistsRepository {
        return repositoryFactory.createSetlistsRepository(RepositoryFactory.RepositoryProvider.ROOM)
    }

    @Provides
    @Singleton
    fun provideCategoriesRepository(repositoryFactory: RepositoryFactory): CategoriesRepository {
        return repositoryFactory.createCategoriesRepository(RepositoryFactory.RepositoryProvider.ROOM)
    }

    @Provides
    @Singleton
    fun provideDataTransferRepository(repositoryFactory: RepositoryFactory): DataTransferRepository {
        return repositoryFactory.createDataTransferRepository(RepositoryFactory.RepositoryProvider.ROOM)
    }
}