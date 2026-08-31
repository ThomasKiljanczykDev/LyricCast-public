package dev.thomas_kiljanczyk.lyriccast.core.nearby.di

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.core.nearby.GmsNearbyPayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NearbyModule {

    @Binds
    @Singleton
    internal abstract fun bindsPayloadTransport(impl: GmsNearbyPayloadTransport): PayloadTransport

    companion object {
        @Provides
        fun provideConnectionsClient(@ApplicationContext context: Context): ConnectionsClient {
            return Nearby.getConnectionsClient(context)
        }
    }
}
