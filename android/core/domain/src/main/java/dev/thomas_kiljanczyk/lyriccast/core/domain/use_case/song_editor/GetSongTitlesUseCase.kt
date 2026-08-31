package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.song_editor

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSongTitlesUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return songsRepository.getAllSongs()
            .map { songs -> songs.map { it.title }.toSet() }
    }
}
