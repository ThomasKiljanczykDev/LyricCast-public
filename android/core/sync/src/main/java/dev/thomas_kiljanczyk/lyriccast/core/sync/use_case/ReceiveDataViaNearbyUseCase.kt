package dev.thomas_kiljanczyk.lyriccast.core.sync.use_case

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.DataTransferRepository
import dev.thomas_kiljanczyk.lyriccast.core.model.ImportOptions
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import dev.thomas_kiljanczyk.lyriccast.core.sync.GmsSyncMessage
import dev.thomas_kiljanczyk.lyriccast.core.sync.R
import dev.thomas_kiljanczyk.lyriccast.core.sync.toTransferData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for receiving and importing application data from GMS Nearby Connections.
 */
class ReceiveDataViaNearbyUseCase @Inject constructor(
    private val dataTransferRepository: DataTransferRepository
) {
    companion object {
        private const val TAG = "ReceiveDataViaNearbyUseCase"
    }

    /**
     * Imports received sync data with the specified import options
     *
     * @param importOptions Options specifying how to handle conflicts (replace, skip, etc.)
     */
    operator fun invoke(
        syncMessage: GmsSyncMessage,
        importOptions: ImportOptions
    ): Flow<UiText> = flow {
        try {
            emit(UiText.StringResource(R.string.sync_validating_data))

            if (syncMessage.version != GmsSyncMessage.CURRENT_VERSION) {
                Log.e(TAG, "Unsupported sync message version: ${syncMessage.version}")
                emit(UiText.StringResource(R.string.sync_error_version_mismatch))
                return@flow
            }

            if (syncMessage.songDtos == null) {
                Log.e(TAG, "Received sync message with no data")
                emit(UiText.StringResource(R.string.sync_error_no_data))
                return@flow
            }

            emit(UiText.StringResource(R.string.sync_importing_data))

            val transferData = syncMessage.toTransferData()

            Log.d(
                TAG, "Importing sync data with ${transferData.songDtos?.size ?: 0} songs, " +
                    "${transferData.categoryDtos?.size ?: 0} categories, " +
                    "${transferData.setlistDtos?.size ?: 0} setlists"
            )

            dataTransferRepository.importData(transferData, importOptions).collect { messageId ->
                emit(UiText.StringResource(messageId))
            }

            emit(UiText.StringResource(R.string.sync_import_successful))
        } catch (e: Exception) {
            Log.e(TAG, "Error importing sync data", e)
            emit(UiText.StringResource(R.string.sync_error_import_failed))
        }
    }
}
