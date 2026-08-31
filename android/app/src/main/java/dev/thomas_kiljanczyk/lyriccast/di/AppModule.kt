package dev.thomas_kiljanczyk.lyriccast.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.application.settingsDataStore
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.cast.MessageTransport
import dev.thomas_kiljanczyk.lyriccast.core.cast.SlidePresentationBus
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.playback.PlaybackController
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import dev.thomas_kiljanczyk.lyriccast.shared.misc.PlaybackControllerImpl
import dev.thomas_kiljanczyk.lyriccast.shared.misc.SlidePresentationBusImpl
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<AppSettings> {
        return context.settingsDataStore
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
