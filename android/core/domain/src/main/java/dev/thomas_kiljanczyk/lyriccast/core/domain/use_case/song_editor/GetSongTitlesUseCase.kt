/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 10:38 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:27 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.song_editor

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SongsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving all song titles for validation purposes.
 */
class GetSongTitlesUseCase @Inject constructor(
    private val songsRepository: SongsRepository
) {
    /**
     * Returns a flow of all song titles in the repository.
     *
     * @return Flow emitting a set of all song titles
     */
    operator fun invoke(): Flow<Set<String>> {
        return songsRepository.getAllSongs()
            .map { songs -> songs.map { it.title }.toSet() }
    }
}
