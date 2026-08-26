/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:38 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.use_case.main

import dev.thomas_kiljanczyk.lyriccast.R
import dev.thomas_kiljanczyk.lyriccast.common.helpers.FileHelper
import dev.thomas_kiljanczyk.lyriccast.datamodel.repositiories.DataTransferRepository
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Use case for exporting all application data (songs, categories, setlists).
 */
class ExportDataUseCase @Inject constructor(
    private val dataTransferRepository: DataTransferRepository
) {
    /**
     * Exports all data to a ZIP file.
     *
     * @param cacheDir The cache directory path for temporary file storage
     * @param outputStream The output stream to write the exported ZIP file to
     * @return Flow emitting progress messages
     */
    operator fun invoke(
        cacheDir: String,
        outputStream: OutputStream
    ): Flow<Int> = flow {
        withContext(Dispatchers.IO) {
            val exportData = dataTransferRepository.getDatabaseTransferData()

            val exportDir = File(cacheDir, ".export")
            exportDir.deleteRecursively()
            exportDir.mkdirs()

            try {
                emit(R.string.export_saving_json)
                val songsString = Json.encodeToString(exportData.songDtos)
                val categoriesString = Json.encodeToString(exportData.categoryDtos)
                val setlistsString = Json.encodeToString(exportData.setlistDtos)

                File(exportDir, "songs.json").writeText(songsString)
                File(exportDir, "categories.json").writeText(categoriesString)
                File(exportDir, "setlists.json").writeText(setlistsString)

                emit(R.string.export_creating_zip)
                FileHelper.zip(outputStream, exportDir.path)

                emit(R.string.export_cleaning_temp)
            } finally {
                exportDir.deleteRecursively()
            }
        }
    }
}
