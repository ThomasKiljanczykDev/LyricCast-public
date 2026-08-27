/*
 * Created by Tomasz Kiljanczyk on 8/27/26, 12:00 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/27/26, 12:00 PM
 */

package dev.thomas_kiljanczyk.lyriccast.common.io

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Opens the streams behind the URIs the system file pickers hand back.
 *
 * This exists so a picked URI, rather than an already-open stream, is what crosses from a
 * composable into a ViewModel: opening a provider stream is a binder call, and closing it flushes.
 * Both belong on the IO dispatcher, and neither can be done from a composable without hard-coding
 * one. The block form keeps `open`, the body and `close` inside the same `withContext`.
 */
@Singleton
class UriStreamDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    /** The cache directory the import and export pipelines stage their temporary files in. */
    suspend fun cacheDirPath(): String = withContext(ioDispatcher) {
        context.cacheDir.canonicalPath
    }

    suspend fun <T> withInputStream(uri: Uri, block: suspend (InputStream) -> T): T =
        withContext(ioDispatcher) {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Could not open input stream for $uri")
            inputStream.use { block(it) }
        }

    suspend fun <T> withOutputStream(uri: Uri, block: suspend (OutputStream) -> T): T =
        withContext(ioDispatcher) {
            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: throw IOException("Could not open output stream for $uri")
            outputStream.use { block(it) }
        }
}
