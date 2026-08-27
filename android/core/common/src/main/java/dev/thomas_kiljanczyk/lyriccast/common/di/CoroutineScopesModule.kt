/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 8:53 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 8:49 PM
 */

package dev.thomas_kiljanczyk.lyriccast.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(
        @Dispatcher(LyricCastDispatchers.Default) dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}
