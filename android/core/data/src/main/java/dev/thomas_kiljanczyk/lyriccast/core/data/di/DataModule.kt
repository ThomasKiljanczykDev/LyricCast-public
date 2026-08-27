/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:22 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepositoryRoomImpl
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepositoryRoomImpl
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepositoryRoomImpl
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepositoryRoomImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    internal abstract fun bindsSongsRepository(
        impl: SongsRepositoryRoomImpl
    ): SongsRepository

    @Binds
    @Singleton
    internal abstract fun bindsSetlistsRepository(
        impl: SetlistsRepositoryRoomImpl
    ): SetlistsRepository

    @Binds
    @Singleton
    internal abstract fun bindsCategoriesRepository(
        impl: CategoriesRepositoryRoomImpl
    ): CategoriesRepository

    @Binds
    @Singleton
    internal abstract fun bindsDataTransferRepository(
        impl: DataTransferRepositoryRoomImpl
    ): DataTransferRepository
}
