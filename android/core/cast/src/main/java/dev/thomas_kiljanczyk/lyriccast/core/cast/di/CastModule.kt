/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 8:53 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 8:50 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast.di

import android.content.Context
import com.google.android.gms.cast.framework.CastContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.core.cast.MessageTransport
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CastModule {

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
