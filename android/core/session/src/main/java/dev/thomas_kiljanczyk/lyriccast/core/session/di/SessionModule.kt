package dev.thomas_kiljanczyk.lyriccast.core.session.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodecImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SessionModule {

    @Binds
    @Singleton
    internal abstract fun bindsSessionMessageCodec(
        impl: SessionMessageCodecImpl
    ): SessionMessageCodec
}
