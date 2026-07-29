/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 11:14 PM
 */

package dev.thomas_kiljanczyk.lyriccast.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.datamodel.di.DataModelModule
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.repositories.CategoriesRepositoryFakeImpl
import dev.thomas_kiljanczyk.lyriccast.repositories.DataTransferRepositoryFakeImpl
import dev.thomas_kiljanczyk.lyriccast.repositories.SetlistsRepositoryFakeImpl
import dev.thomas_kiljanczyk.lyriccast.repositories.SongsRepositoryFakeImpl
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModelModule::class]
)
object FakeDataModelModule {
    @Provides
    @Singleton
    fun provideSongsRepository(): SongsRepository = SongsRepositoryFakeImpl()

    @Provides
    @Singleton
    fun provideSetlistsRepository(): SetlistsRepository = SetlistsRepositoryFakeImpl()

    @Provides
    @Singleton
    fun provideCategoriesRepository(): CategoriesRepository = CategoriesRepositoryFakeImpl()

    @Provides
    @Singleton
    fun provideDataTransferRepository(): DataTransferRepository =
        DataTransferRepositoryFakeImpl(
            provideSongsRepository(),
            provideSetlistsRepository(),
            provideCategoriesRepository()
        )
}