/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 3:45 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 2:02 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.session

import android.util.Log
import com.github.luben.zstd.Zstd
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Compresses and decompresses data using Zstd compression.
 * Provides high compression ratios and fast decompression suitable for reducing message sizes in network communication.
 */
internal class ZstdCompressionUtil @Inject constructor(
    @param:Dispatcher(LyricCastDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "ZstdCompressionUtil"

        // Level 3 provides good balance between speed and compression
        const val COMPRESSION_LEVEL = 3
    }

    /**
     * Compresses a string using Zstd compression.
     *
     * @param data The string to compress
     * @return Compressed byte array, or null if compression fails
     */
    suspend fun compress(data: String): ByteArray? {
        return try {
            val inputBytes = data.toByteArray(Charsets.UTF_8)
            compress(inputBytes)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to compress string", e)
            null
        }
    }

    /**
     * Compresses a byte array using Zstd compression.
     *
     * Uses compression level 3 for a good balance between compression ratio and speed.
     * Level ranges from 1 (fastest) to 22 (best compression).
     *
     * @param data The byte array to compress
     * @return Compressed byte array, or null if compression fails
     */
    suspend fun compress(data: ByteArray): ByteArray? {
        return withContext(defaultDispatcher) {
            try {
                Zstd.compress(data, COMPRESSION_LEVEL)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to compress data", e)
                null
            } catch (e: LinkageError) {
                // Native zstd-jni library unavailable (e.g. on JVM unit tests). Caller falls
                // back to sending the payload uncompressed; receivers tolerate both forms.
                Log.e(TAG, "Native zstd library not available", e)
                null
            }
        }
    }

    /**
     * Decompresses Zstd-compressed data back to a string.
     *
     * @param compressedData The compressed byte array
     * @return Decompressed string, or null if decompression fails
     */
    suspend fun decompressToString(compressedData: ByteArray): String? {
        return try {
            val decompressed = decompress(compressedData)
            decompressed?.toString(Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decompress to string", e)
            null
        }
    }

    /**
     * Decompresses Zstd-compressed data back to a byte array.
     *
     * @param compressedData The compressed byte array
     * @return Decompressed byte array, or null if decompression fails
     */
    suspend fun decompress(compressedData: ByteArray): ByteArray? {
        return withContext(defaultDispatcher) {
            try {
                Zstd.decompress(compressedData)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decompress data", e)
                null
            } catch (e: LinkageError) {
                Log.e(TAG, "Native zstd library not available", e)
                null
            }
        }
    }
}
