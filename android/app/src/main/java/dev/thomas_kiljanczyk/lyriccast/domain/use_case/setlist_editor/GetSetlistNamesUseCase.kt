/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 11:00 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 10:45 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.setlist_editor

import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.SetlistsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for retrieving all setlist names for validation purposes.
 */
class GetSetlistNamesUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository
) {
    /**
     * Returns a flow of all setlist names in the repository.
     *
     * @return Flow emitting a set of all setlist names
     */
    operator fun invoke(): Flow<Set<String>> {
        return setlistsRepository.getAllSetlists()
            .map { setlists -> setlists.map { it.name }.toSet() }
    }
}
