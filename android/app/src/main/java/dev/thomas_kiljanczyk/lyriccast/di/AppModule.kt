/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 2:43 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 2:24 PM
 */

package dev.thomas_kiljanczyk.lyriccast.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.application.AppSettings
import dev.thomas_kiljanczyk.lyriccast.application.settingsDataStore
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.GmsNearbySessionServerContext
import dev.thomas_kiljanczyk.lyriccast.shared.misc.LyricCastMessagingContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideCastContext(@ApplicationContext context: Context): CastContext {
        return CastContext.getSharedInstance(context)
    }

    @Provides
    fun provideConnectionsClient(@ApplicationContext context: Context): ConnectionsClient {
        return Nearby.getConnectionsClient(context)
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<AppSettings> {
        return context.settingsDataStore
    }

    @Provides
    @Singleton
    fun provideCastMessagingContext(castContext: CastContext): CastMessagingContext {
        return CastMessagingContext(castContext)
    }

    @Provides
    @Singleton
    fun provideGmsNearbyServerContext(
        connectionsClient: ConnectionsClient
    ): GmsNearbySessionServerContext {
        return GmsNearbySessionServerContext(connectionsClient)
    }

    @Provides
    @Singleton
    fun provideLyricCastMessagingContext(
        castMessagingContext: CastMessagingContext,
        gmsNearbySessionServerContext: GmsNearbySessionServerContext
    ): LyricCastMessagingContext {
        return LyricCastMessagingContext(castMessagingContext, gmsNearbySessionServerContext)
    }
}
