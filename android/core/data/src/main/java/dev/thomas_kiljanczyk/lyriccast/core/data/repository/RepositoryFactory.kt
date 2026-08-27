/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:21 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.database.LyricCastDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryFactory @Inject constructor(
    private val appDatabase: LyricCastDatabase
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
