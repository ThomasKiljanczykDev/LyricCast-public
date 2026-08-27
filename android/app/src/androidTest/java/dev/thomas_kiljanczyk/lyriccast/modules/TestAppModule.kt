/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 11:14 PM
 */

package dev.thomas_kiljanczyk.lyriccast.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.application.AppSettingsSerializer
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.common.helpers.UUIDv7
import dev.thomas_kiljanczyk.lyriccast.core.cast.MessageTransport
import dev.thomas_kiljanczyk.lyriccast.core.cast.SlidePresentationBus
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackController
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import dev.thomas_kiljanczyk.lyriccast.di.AppModule
import dev.thomas_kiljanczyk.lyriccast.shared.misc.PlaybackControllerImpl
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SlidePresentationBusImpl
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
class TestAppModule {

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
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<AppSettings> {
        if (dataStore == null) {
            initializeDataStore(appContext)
        }

        return dataStore!!
    }

    @Provides
    @Singleton
    fun provideSlidePresentationBus(
        castMessageTransport: MessageTransport,
        payloadTransport: PayloadTransport,
        codec: SessionMessageCodec,
        @ApplicationScope scope: CoroutineScope,
        @Dispatcher(LyricCastDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): SlidePresentationBus {
        return SlidePresentationBusImpl(
            castMessageTransport,
            payloadTransport,
            codec,
            scope,
            ioDispatcher
        )
    }

    @Provides
    fun providePlaybackController(
        dataStore: DataStore<AppSettings>,
        bus: SlidePresentationBus,
        payloadTransport: PayloadTransport,
        codec: SessionMessageCodec,
        @Dispatcher(LyricCastDispatchers.Main) mainDispatcher: CoroutineDispatcher,
        @Dispatcher(LyricCastDispatchers.Default) defaultDispatcher: CoroutineDispatcher
    ): PlaybackController {
        return PlaybackControllerImpl(
            dataStore,
            bus,
            payloadTransport,
            codec,
            mainDispatcher,
            defaultDispatcher
        )
    }
}
