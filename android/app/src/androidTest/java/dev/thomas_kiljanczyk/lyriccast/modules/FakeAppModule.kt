/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 11:14 PM
 */

package dev.thomas_kiljanczyk.lyriccast.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.nearby.connection.ConnectionsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.application.AppSettings
import dev.thomas_kiljanczyk.lyriccast.application.AppSettingsSerializer
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.di.AppModule
import dev.thomas_kiljanczyk.lyriccast.shared.cast.CastMessagingContext
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.ConnectionsClientFakeImpl
import dev.thomas_kiljanczyk.lyriccast.shared.gms_nearby.GmsNearbySessionServerContext
import dev.thomas_kiljanczyk.lyriccast.shared.misc.LyricCastMessagingContext
import java.io.File
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
class FakeAppModule {

    companion object {
        private const val TEST_DATASTORE_FILENAME = "settings"

        var dataStore: DataStore<AppSettings>? = null
        var dataStoreFile: File? = null

        fun initializeDataStore(appContext: Context) {
            if (dataStoreFile == null) {
                cleanupDataStore()
            }

            val newDataStoreFile =
                appContext.dataStoreFile("$TEST_DATASTORE_FILENAME-${UUIDv7.randomUUID()}")
            dataStoreFile = newDataStoreFile

            dataStore = DataStoreFactory.create(
                produceFile = { newDataStoreFile },
                serializer = AppSettingsSerializer
            )
        }

        fun cleanupDataStore() {
            dataStoreFile?.delete()
            dataStoreFile = null
            dataStore = null
        }
    }

    @Provides
    fun provideCastContext(@ApplicationContext context: Context): CastContext {
        return CastContext.getSharedInstance(context)
    }

    @Provides
    fun provideConnectionsClient(): ConnectionsClient {
        return ConnectionsClientFakeImpl()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<AppSettings> {
        if (dataStore == null) {
            initializeDataStore(appContext)
        }

        return dataStore!!
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