package dev.thomas_kiljanczyk.lyriccast.core.data_test.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.data.di.DataModule
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data_test.repository.CategoriesRepositoryFakeImpl
import dev.thomas_kiljanczyk.lyriccast.core.data_test.repository.DataTransferRepositoryFakeImpl
import dev.thomas_kiljanczyk.lyriccast.core.data_test.repository.SetlistsRepositoryFakeImpl
import dev.thomas_kiljanczyk.lyriccast.core.data_test.repository.SongsRepositoryFakeImpl
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {
    @Provides
    @Singleton
    fun provideSongsRepository(): SongsRepository = SongsRepositoryFakeImpl()

    @Provides
    @Singleton
    fun provideSetlistsRepository(): SetlistsRepository = SetlistsRepositoryFakeImpl()

    @Provides
    @Singleton
    fun provideCategoriesRepository(): CategoriesRepository = CategoriesRepositoryFakeImpl()

    @Provides
    @Singleton
    fun provideDataTransferRepository(
        @Dispatcher(LyricCastDispatchers.Default) defaultDispatcher: CoroutineDispatcher
    ): DataTransferRepository =
        DataTransferRepositoryFakeImpl(
            provideSongsRepository(),
            provideSetlistsRepository(),
            provideCategoriesRepository(),
            defaultDispatcher
        )
}
