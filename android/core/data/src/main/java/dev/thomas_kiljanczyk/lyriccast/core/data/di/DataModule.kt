/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:22 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.RepositoryFactory
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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
