package dev.thomas_kiljanczyk.lyriccast.core.data.repository

import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import kotlinx.coroutines.flow.Flow

interface DataTransferRepository {

    suspend fun clearDatabase()

    suspend fun importData(
        data: DatabaseTransferData,
        options: ImportOptions
    ): Flow<Int>

    suspend fun importSongs(
        songDtoSet: Set<SongDto>,
        options: ImportOptions
    ): Flow<Int>

    suspend fun getDatabaseTransferData(): DatabaseTransferData
}
