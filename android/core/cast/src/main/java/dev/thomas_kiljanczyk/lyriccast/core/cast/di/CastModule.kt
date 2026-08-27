/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 8:53 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 8:50 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast.di

import android.content.Context
import android.util.Log
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
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
    private const val TAG = "CastModule"

    @Provides
    fun provideCastContext(@ApplicationContext context: Context): CastContext? {
        val castApi = GoogleApiAvailability.getInstance()
        if (castApi.isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            return null
        }

        return try {
            CastContext.getSharedInstance(context)
        } catch (e: Exception) {
            Log.e(TAG, "Cast framework not available", e)
            null
        }
    }

    @Provides
    @Singleton
    fun provideMessageTransport(
        castContext: CastContext?,
        @Dispatcher(LyricCastDispatchers.Main) mainDispatcher: CoroutineDispatcher
    ): MessageTransport? {
        return castContext?.let { CastMessagingContext(it, mainDispatcher) }
    }
}
