/*
 * Created by Tomasz Kiljanczyk on 6/7/26, 7:21 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 6/7/26, 7:18 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast_test.di

import android.content.Context
import com.google.android.gms.cast.framework.CastContext
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.core.cast.MessageTransport
import dev.thomas_kiljanczyk.lyriccast.core.cast.di.CastModule
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CastModule::class]
)
object TestCastModule {

    @Provides
    fun provideCastContext(@ApplicationContext context: Context): CastContext {
        return CastContext.getSharedInstance(context)
    }

    @Provides
    @Singleton
    fun provideMessageTransport(
        castContext: CastContext,
        @Dispatcher(LyricCastDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): MessageTransport {
        return CastMessagingContext(castContext, mainDispatcher)
    }
}
