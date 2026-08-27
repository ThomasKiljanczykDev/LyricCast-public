/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 11:14 PM
 */

package dev.thomas_kiljanczyk.lyriccast.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
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
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.CURRENT_ONBOARDING_VERSION
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
object TestAppModule {
    private const val TEST_DATASTORE_FILENAME = "settings"

    var dataStore: DataStore<AppSettings>? = null
    var dataStoreFile: File? = null

    /**
     * Defaults to "already onboarded":
     * tests start from a blank DataStore and an empty library,
     * exactly the state that triggers the tutorial,
     * so every UI test would otherwise open on the carousel.
     *
     * Tests that want the tutorial set this before launching.
     */
    var initialOnboardingVersion: Int = CURRENT_ONBOARDING_VERSION

    private val testSerializer = object : Serializer<AppSettings> by AppSettingsSerializer {
        override val defaultValue: AppSettings
            get() = AppSettingsSerializer.defaultValue.toBuilder()
                .setOnboardingCompletedVersion(initialOnboardingVersion)
                .build()
    }

    fun initializeDataStore(appContext: Context): DataStore<AppSettings> {
        dataStoreFile?.delete()

        val newDataStoreFile =
            appContext.dataStoreFile("$TEST_DATASTORE_FILENAME-${UUIDv7.randomUUID()}")
        dataStoreFile = newDataStoreFile

        return DataStoreFactory.create(
            produceFile = { newDataStoreFile },
            serializer = testSerializer
        )
    }

    fun cleanupDataStore() {
        dataStoreFile?.delete()
        dataStoreFile = null
        dataStore = null
        initialOnboardingVersion = CURRENT_ONBOARDING_VERSION
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<AppSettings> {
        var dataStore = dataStore
        if (dataStore == null) {
            dataStore = initializeDataStore(appContext)
            this.dataStore = dataStore
        }

        return dataStore
    }

    @Provides
    @Singleton
    fun provideSlidePresentationBus(
        castMessageTransport: MessageTransport?,
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
