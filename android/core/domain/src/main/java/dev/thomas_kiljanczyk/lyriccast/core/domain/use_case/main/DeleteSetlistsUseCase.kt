/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 3:41 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 3:35 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.domain.use_case.main

import android.util.Log
import dev.thomas_kiljanczyk.lyriccast.core.data.repository.SetlistsRepository
import dev.thomas_kiljanczyk.lyriccast.core.domain.R
import dev.thomas_kiljanczyk.lyriccast.core.model.DeleteSetlistsResult
import dev.thomas_kiljanczyk.lyriccast.core.model.UiText
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for deleting setlists with proper error handling.
 */
class DeleteSetlistsUseCase @Inject constructor(
    private val setlistsRepository: SetlistsRepository
) {
    private companion object {
        const val TAG = "DeleteSetlistsUseCase"
    }

    /**
     * Deletes the specified setlists.
     *
     * @param setlistIds List of setlist IDs to delete
     * @return Result indicating success or error
     */
    suspend operator fun invoke(
        setlistIds: List<UUID>
    ): DeleteSetlistsResult {
        if (setlistIds.isEmpty()) {
            return DeleteSetlistsResult.Success(0)
        }

        return try {
            setlistsRepository.deleteSetlists(setlistIds)
            DeleteSetlistsResult.Success(setlistIds.size)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete setlists", e)
            DeleteSetlistsResult.Error(
                e.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.delete_setlists_failed)
            )
        }
    }
}
