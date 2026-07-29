/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 7:51 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 7:47 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.main

import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.common.helpers.FileHelper
import dev.thomas_kiljanczyk.lyriccast.datamodel.models.DatabaseTransferData
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SetlistDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import dev.thomas_kiljanczyk.lyriccast.domain.models.SetlistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

/**
 * Use case for exporting selected setlists to a ZIP file.
 */
class ExportSetlistsUseCase @Inject constructor() {
    /**
     * Exports selected setlists to a ZIP file.
     *
     * @param cacheDir The cache directory path for temporary file storage
     * @param outputStream The output stream to write the exported ZIP file to
     * @param selectedSetlists The list of setlists to export
     * @return Flow emitting string resource IDs for progress messages
     */
    operator fun invoke(
        cacheDir: String,
        outputStream: OutputStream,
        selectedSetlists: List<SetlistItem>
    ): Flow<Int> = flow {
        val exportDir = File(cacheDir, ".export")
        exportDir.deleteRecursively()
        exportDir.mkdirs()

        emit(R.string.main_activity_export_preparing_data)

        // Get all songs referenced by the selected setlists
        val allSongsInSetlists = selectedSetlists
            .flatMap { it.presentation }
            .distinctBy { it.id }
        val allCategoriesInSetlists = allSongsInSetlists
            .mapNotNull { it.category }
            .distinctBy { it.id }

        // Create transfer data with selected setlists and their songs
        val transferData = DatabaseTransferData(
            songDtos = allSongsInSetlists.map { songItem ->
                SongDto(
                    title = songItem.title,
                    lyrics = songItem.lyricsMap,
                    presentation = songItem.presentation,
                    category = songItem.category?.name
                )
            },
            categoryDtos = allCategoriesInSetlists.map { category ->
                CategoryDto(
                    name = category.name,
                    color = category.color
                )
            },
            setlistDtos = selectedSetlists.map { setlistItem ->
                SetlistDto(
                    name = setlistItem.name,
                    songs = setlistItem.presentation.map { it.title }
                )
            }
        )

        emit(R.string.main_activity_export_saving_json)
        val songsString = Json.encodeToString(transferData.songDtos?.toList() ?: emptyList())
        val categoriesString =
            Json.encodeToString(transferData.categoryDtos?.toList() ?: emptyList())
        val setlistsString = Json.encodeToString(transferData.setlistDtos?.toList() ?: emptyList())

        File(exportDir, "songs.json").writeText(songsString)
        File(exportDir, "categories.json").writeText(categoriesString)
        File(exportDir, "setlists.json").writeText(setlistsString)

        emit(R.string.main_activity_export_saving_zip)
        FileHelper.zip(outputStream, exportDir.path)

        emit(R.string.main_activity_export_deleting_temp)
        exportDir.deleteRecursively()
    }.flowOn(Dispatchers.Default)
}