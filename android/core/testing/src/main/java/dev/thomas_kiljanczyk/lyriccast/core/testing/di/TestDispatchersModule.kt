package dev.thomas_kiljanczyk.lyriccast.core.testing.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import dev.thomas_kiljanczyk.lyriccast.common.di.CoroutineScopesModule
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.DispatchersModule
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Puts one [TestDispatcher] behind every off-main dispatcher, and builds the
 * [ApplicationScope] on its scheduler so `advanceUntilIdle()` drains the lot.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutineScopesModule::class, DispatchersModule::class]
)
object TestDispatchersModule {

    @Provides
    @Singleton
    fun provideTestDispatcher(): TestDispatcher = UnconfinedTestDispatcher()

    @Provides
    @Dispatcher(LyricCastDispatchers.IO)
    fun provideIoDispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @Dispatcher(LyricCastDispatchers.Default)
    fun provideDefaultDispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher =
        testDispatcher

    // The real one: the Compose test clock and everything posted to the UI thread depend on it.
    @Provides
    @Dispatcher(LyricCastDispatchers.Main)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @ApplicationScope
    fun provideTestApplicationScope(testDispatcher: TestDispatcher): CoroutineScope =
        TestScope(testDispatcher.scheduler)
}
