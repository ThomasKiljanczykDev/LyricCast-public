/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:21 PM
 */

package dev.thomas_kiljanczyk.lyriccast.datamodel

import dev.thomas_kiljanczyk.lyriccast.datamodel.database.AppDatabase
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.impl.room.CategoriesRepositoryRoomImpl
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.impl.room.DataTransferRepositoryRoomImpl
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.impl.room.SetlistsRepositoryRoomImpl
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.impl.room.SongsRepositoryRoomImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryFactory @Inject constructor(
    private val appDatabase: AppDatabase
) {

    fun createSongsRepository(provider: RepositoryProvider): SongsRepository {
        return when (provider) {
            RepositoryProvider.ROOM -> {
                SongsRepositoryRoomImpl(appDatabase.songDao())
            }
        }
    }

    fun createSetlistsRepository(provider: RepositoryProvider): SetlistsRepository {
        return when (provider) {
            RepositoryProvider.ROOM -> {
                SetlistsRepositoryRoomImpl(appDatabase.setlistDao())
            }
        }
    }

    fun createCategoriesRepository(provider: RepositoryProvider): CategoriesRepository {
        return when (provider) {
            RepositoryProvider.ROOM -> {
                CategoriesRepositoryRoomImpl(appDatabase.categoryDao())
            }
        }
    }

    fun createDataTransferRepository(
        provider: RepositoryProvider
    ): DataTransferRepository {
        return when (provider) {
            RepositoryProvider.ROOM -> {
                DataTransferRepositoryRoomImpl(
                    appDatabase.categoryDao(),
                    appDatabase.songDao(),
                    appDatabase.setlistDao()
                )
            }
        }
    }

    enum class RepositoryProvider {
        ROOM
    }
}
