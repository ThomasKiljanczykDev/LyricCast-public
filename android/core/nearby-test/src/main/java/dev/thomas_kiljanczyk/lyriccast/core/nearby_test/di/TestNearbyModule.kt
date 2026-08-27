/*
 * Created by Tomasz Kiljanczyk on 6/7/26, 8:13 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 6/7/26, 8:10 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.nearby_test.di

import com.google.android.gms.nearby.connection.ConnectionsClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.di.NearbyModule
import dev.thomas_kiljanczyk.lyriccast.core.nearby_test.fake.FakePayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby_test.fake.TestConnectionsClient
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NearbyModule::class]
)
abstract class TestNearbyModule {

    @Binds
    @Singleton
    abstract fun bindsPayloadTransport(impl: FakePayloadTransport): PayloadTransport

    companion object {
        @Provides
        @Singleton
        fun provideFakePayloadTransport(): FakePayloadTransport = FakePayloadTransport()

        @Provides
        fun provideConnectionsClient(): ConnectionsClient = TestConnectionsClient()
    }
}
