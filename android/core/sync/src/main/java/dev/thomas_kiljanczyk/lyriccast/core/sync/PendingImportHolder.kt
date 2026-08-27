/*
 * Created by Tomasz Kiljanczyk on 8/24/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/24/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.sync

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.withContext

/**
 * A file handed to the app from outside, waiting for the user to confirm the import via the
 * manual format-picker dialog. Unlike the private reference implementation, this never carries a
 * sniffed/suggested format: this build only supports the LyricCast-export and OpenSong formats,
 * and the user always picks the format explicitly.
 */
data class PendingImport(
    val file: File,
    val displayName: String
)

/** Where a pending import is read from once the user confirms it. */
sealed interface ImportInput {
    /** A file the user picked, still behind a content-provider URI. */
    @JvmInline
    value class FromUri(val uri: Uri) : ImportInput

    /** The file an outside app handed in, already copied into the cache. */
    @JvmInline
    value class FromPendingImport(val pendingImport: PendingImport) : ImportInput
}

/**
 * Carries a file that arrived through an `ACTION_VIEW` or `ACTION_SEND` intent from the activity
 * to the import dialog.
 *
 * The incoming stream is copied into the cache rather than kept as a URI: the read grant on an
 * inbound intent is not persistable, and the file may sit here while the user finishes any
 * in-progress dialog interaction.
 */
@Singleton
class PendingImportHolder @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    private val _pendingImport = MutableStateFlow<PendingImport?>(null)
    val pendingImport: StateFlow<PendingImport?> = _pendingImport.asStateFlow()

    private val importDirectory: File
        get() = File(context.cacheDir, IMPORT_DIRECTORY_NAME)

    /**
     * Copies the content behind [uri] into the cache and publishes it for the import dialog.
     *
     * @return True if the file was copied and published.
     */
    suspend fun offer(uri: Uri): Boolean = withContext(ioDispatcher) {
        val displayName = queryDisplayName(uri) ?: DEFAULT_FILE_NAME

        // The previous copy is superseded, and this is the only place one is deleted: doing it on
        // dismissal would race the import, which opens the file after the dialog is already gone.
        _pendingImport.getAndUpdate { null }?.file?.delete()
        importDirectory.mkdirs()

        val target = File(importDirectory, displayName.substringAfterLast('/'))
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use(input::copyTo)
            } ?: error("Could not open input stream for $uri")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy the incoming file", e)
            target.delete()
            return@withContext false
        }

        _pendingImport.value = PendingImport(target, displayName)
        true
    }

    /**
     * Stops offering the pending file, once the user has imported it or dismissed the dialog.
     *
     * The cached copy itself is left behind and reclaimed by the next [offer]; deleting it here
     * would pull the file out from under an import that has only just started.
     */
    fun clear() {
        _pendingImport.value = null
    }

    private fun queryDisplayName(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
        }
    }

    private companion object {
        const val TAG = "PendingImportHolder"
        const val IMPORT_DIRECTORY_NAME = "pending-import"
        const val DEFAULT_FILE_NAME = "import.zip"
    }
}
