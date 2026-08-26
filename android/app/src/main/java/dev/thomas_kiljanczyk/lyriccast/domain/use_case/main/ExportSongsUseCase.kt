/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:42 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:30 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.main

import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.common.helpers.FileHelper
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.DatabaseTransferData
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import dev.thomas_kiljanczyk.lyriccast.domain.models.SongItem
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

/**
 * Use case for exporting selected songs to a ZIP file.
 */
class ExportSongsUseCase @Inject constructor() {
    /**
     * Exports selected songs to a ZIP file.
     *
     * @param cacheDir The cache directory path for temporary file storage
     * @param outputStream The output stream to write the exported ZIP file to
     * @param selectedSongs The list of songs to export
     * @return Flow emitting string resource IDs for progress messages
     */
    operator fun invoke(
        cacheDir: String,
        outputStream: OutputStream,
        selectedSongs: List<SongItem>
    ): Flow<Int> = flow {
        val exportDir = File(cacheDir, ".export")
        exportDir.deleteRecursively()
        exportDir.mkdirs()

        emit(R.string.main_activity_export_preparing_data)

        // Create transfer data with only selected songs
        val transferData = DatabaseTransferData(
            songDtos = selectedSongs.map { songItem ->
                SongDto(
                    title = songItem.title,
                    lyrics = songItem.lyricsMap,
                    presentation = songItem.presentation,
                    category = songItem.category?.name
                )
            },
            categoryDtos = selectedSongs.mapNotNull { it.category }.distinctBy { it.id }
                .map { category ->
                    CategoryDto(
                        name = category.name,
                        color = category.color
                    )
                },
            setlistDtos = null // Don't export setlists for selected songs
        )

        emit(R.string.main_activity_export_saving_json)
        val songsString = Json.encodeToString(transferData.songDtos?.toList() ?: emptyList())
        val categoriesString =
            Json.encodeToString(transferData.categoryDtos?.toList() ?: emptyList())

        File(exportDir, "songs.json").writeText(songsString)
        File(exportDir, "categories.json").writeText(categoriesString)

        emit(R.string.main_activity_export_saving_zip)
        FileHelper.zip(outputStream, exportDir.path)

        emit(R.string.main_activity_export_deleting_temp)
        exportDir.deleteRecursively()
    }.flowOn(Dispatchers.Default)
}
