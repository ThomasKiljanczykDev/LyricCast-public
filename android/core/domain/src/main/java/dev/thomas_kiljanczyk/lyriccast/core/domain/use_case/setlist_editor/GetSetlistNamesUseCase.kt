package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.setlist_editor

import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSetlistNamesUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return setlistsRepository.getAllSetlists()
            .map { setlists -> setlists.map { it.name }.toSet() }
    }
}
