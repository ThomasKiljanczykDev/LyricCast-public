/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:52 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:43 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.common.helpers.FileHelper
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DatabaseTransferData
import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.ImportFormat
import dev.thomas_kiljanczyk.lyriccast.datatransfer.enums.SongXmlParserType
import dev.thomas_kiljanczyk.lyriccast.datatransfer.factories.ImportSongXmlParserFactory
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SetlistDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Use case for importing data from various formats into the application.
 * Handles both LyricCast and OpenSong formats.
 */
class ImportDataUseCase @Inject constructor(
    private val dataTransferRepository: DataTransferRepository
) {
    companion object {
        private const val TAG = "ImportDataUseCase"
    }

    /**
     * Imports data from the specified format.
     *
     * @param cacheDir The cache directory path for temporary file storage
     * @param inputStream The input stream containing the data to import
     * @param format The format of the data being imported
     * @param options Import options specifying how to handle conflicts
     * @return Flow emitting resource IDs for progress messages, or null if import fails
     */
    suspend operator fun invoke(
        cacheDir: String,
        inputStream: InputStream,
        format: ImportFormat,
        options: ImportOptions
    ): Flow<UiText>? {
        return when (format) {
            ImportFormat.LYRIC_CAST -> importLyricCast(cacheDir, inputStream, options)
            ImportFormat.OPEN_SONG -> importOpenSong(cacheDir, inputStream, options)
            else -> null
        }
    }

    private suspend fun importLyricCast(
        cacheDir: String,
        inputStream: InputStream,
        importOptions: ImportOptions
    ): Flow<UiText>? {
        val transferData: DatabaseTransferData =
            getImportData(cacheDir, inputStream) ?: return null
        return dataTransferRepository.importData(transferData, importOptions)
            .map { UiText.StringResource(it) }
    }

    private suspend fun importOpenSong(
        cacheDir: String,
        inputStream: InputStream,
        importOptions: ImportOptions
    ): Flow<UiText>? {
        val importDir = File(cacheDir)
        val importSongXmlParser =
            ImportSongXmlParserFactory.create(importDir, SongXmlParserType.OPEN_SONG)

        val importedSongs: Set<SongDto> = try {
            importSongXmlParser.parseZip(inputStream)
        } catch (exception: Exception) {
            Log.e(TAG, "Error while importing songs", exception)
            null
        } ?: return null

        return dataTransferRepository.importSongs(importedSongs, importOptions)
            .map { UiText.StringResource(it) }
    }

    private fun getImportData(cacheDir: String, inputStream: InputStream): DatabaseTransferData? {
        val importDir = File(cacheDir, ".import")
        importDir.deleteRecursively()
        importDir.mkdirs()

        FileHelper.unzip(inputStream, importDir.path)

        return try {
            val songsJson = File(importDir, "songs.json").readText()
            val categoriesJson = File(importDir, "categories.json").readText()

            val setlistsFile = File(importDir, "setlists.json")
            val setlistsJson: String? = if (setlistsFile.exists()) {
                File(importDir, "setlists.json").readText()
            } else {
                null
            }

            val songDtos = Json.decodeFromString<List<SongDto>>(songsJson)
            val categoryDtos = Json.decodeFromString<List<CategoryDto>>(categoriesJson)
            val setlistDtos = setlistsJson?.let {
                Json.decodeFromString<List<SetlistDto>>(it)
            }

            DatabaseTransferData(
                songDtos = songDtos,
                categoryDtos = categoryDtos,
                setlistDtos = setlistDtos
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Error while importing data", exception)
            null
        } finally {
            importDir.deleteRecursively()
        }
    }
}
