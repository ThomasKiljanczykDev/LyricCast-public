/*
 * Created by Tomasz Kiljanczyk on 5/27/26, 11:30 AM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 5/27/26, 11:30 AM
 */

package dev.thomas_kiljanczyk.lyriccast.core.sync.use_case

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.core.nearby.ConnectionState
import dev.thomas_kiljanczyk.lyriccast.core.nearby.PayloadTransport
import dev.thomas_kiljanczyk.lyriccast.core.nearby.SyncTimeoutException
import dev.thomas_kiljanczyk.lyriccast.core.session.SessionMessageCodec
import dev.thomas_kiljanczyk.lyriccast.core.session.encode
import dev.thomas_kiljanczyk.lyriccast.core.sync.R
import dev.thomas_kiljanczyk.lyriccast.core.sync.toSyncMessage
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Use case for sending all application data via GMS Nearby Connections.
 * Exports the whole database and broadcasts it to connected receivers.
 */
class SendDataViaNearbyUseCase @Inject constructor(
    private val dataTransferRepository: DataTransferRepository,
    private val payloadTransport: PayloadTransport,
    private val codec: SessionMessageCodec,
    @param:Dispatcher(LyricCastDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val TAG = "SendDataViaNearbyUseCase"
        private val CONNECTION_TIMEOUT = 60.seconds
    }

    /**
     * Sends the whole application database via GMS Nearby.
     *
     * @param deviceName The name of this device (for sync metadata)
     * @return Flow emitting progress message resource IDs
     */
    operator fun invoke(deviceName: String): Flow<Int> = flow {
        emit(R.string.sync_preparing_data)

        val exportData = withContext(ioDispatcher) {
            dataTransferRepository.getDatabaseTransferData()
        }

        emit(R.string.sync_starting_broadcast)

        Log.d(TAG, "Waiting for receiver device to connect...")
        emit(R.string.sync_connecting)

        val connected = withTimeoutOrNull(CONNECTION_TIMEOUT) {
            try {
                payloadTransport.deviceConnectionInfo
                    .filter { it.connectionState == ConnectionState.CONNECTED }
                    .first()
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error waiting for connection", e)
                false
            }
        }

        if (connected != true) {
            Log.e(TAG, "No receiver device connected within timeout period")
            throw SyncTimeoutException("No device connected for sync - advertising expired")
        }

        Log.d(TAG, "Receiver device connected, proceeding with sync")

        val syncMessage = exportData.toSyncMessage(deviceName)

        Log.d(
            TAG, "Sending sync data with ${exportData.songDtos?.size ?: 0} songs, " +
                "${exportData.categoryDtos?.size ?: 0} categories, " +
                "${exportData.setlistDtos?.size ?: 0} setlists"
        )

        payloadTransport.broadcast(codec.encode(syncMessage))

        emit(R.string.sync_data_sent)
    }
}
