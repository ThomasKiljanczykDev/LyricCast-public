/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 4:15 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 4:01 PM
 */

package dev.thomas_kiljanczyk.lyriccast.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.data.LocaleManager
import dev.thomas_kiljanczyk.lyriccast.feature.settings.impl.domain.LocaleManager as LocaleManagerInterface
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocaleModule {

    @Binds
    @Singleton
    abstract fun bindLocaleManager(localeManager: LocaleManager): LocaleManagerInterface
}
