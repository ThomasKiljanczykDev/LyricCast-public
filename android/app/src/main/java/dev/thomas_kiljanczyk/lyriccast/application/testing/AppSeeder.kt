package dev.thomas_kiljanczyk.lyriccast.application.testing

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.CategoriesRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.settings.SettingsRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.Category
import dev.thomas_kiljanczyk.lyriccast.core.model.Setlist
import dev.thomas_kiljanczyk.lyriccast.core.model.Song
import dev.thomas_kiljanczyk.lyriccast.core.tutorial.CURRENT_ONBOARDING_VERSION
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppSeeder @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val songsRepository: SongsRepository,
    private val setlistsRepository: SetlistsRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend fun seed() {
        val categories = (1..5).map { index -> Category(name = "Category $index") }
        categories.forEach { categoriesRepository.upsertCategory(it) }

        val songs = (1..20).map { index ->
            Song(
                title = "Song $index",
                lyrics = listOf(Song.LyricsSection(name = "Verse 1", text = "Lyrics for song $index")),
                presentation = listOf("Verse 1"),
                category = categories[index % categories.size]
            )
        }
        songs.forEach { songsRepository.upsertSong(it) }

        val setlists = (1..3).map { index ->
            Setlist(name = "Setlist $index", presentation = songs.take(5))
        }
        setlists.forEach { setlistsRepository.upsertSetlist(it) }

        settingsRepository.updateOnboardingCompletedVersion(CURRENT_ONBOARDING_VERSION)
    }
}
