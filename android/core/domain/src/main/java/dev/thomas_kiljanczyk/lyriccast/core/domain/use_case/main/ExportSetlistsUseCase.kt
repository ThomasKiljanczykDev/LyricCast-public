package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main

import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.common.helpers.FileHelper
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DatabaseTransferData
import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.SetlistItem
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.CategoryDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SetlistDto
import dev.thomas_kiljanczyk.lyriccast.datatransfer.models.SongDto
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

class ExportSetlistsUseCase @Inject constructor(
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        cacheDir: String,
        outputStream: OutputStream,
        selectedSetlists: List<SetlistItem>
    ): Flow<Int> = flow {
        val exportDir = File(cacheDir, ".export")
        exportDir.deleteRecursively()
        exportDir.mkdirs()

        emit(R.string.main_activity_export_preparing_data)

        val allSongsInSetlists = selectedSetlists
            .flatMap { it.presentation }
            .distinctBy { it.id }
        val allCategoriesInSetlists = allSongsInSetlists
            .mapNotNull { it.category }
            .distinctBy { it.id }

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
    }.flowOn(ioDispatcher)
}
