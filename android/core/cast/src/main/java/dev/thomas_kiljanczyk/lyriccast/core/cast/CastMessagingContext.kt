/*
 * Created by Tomasz Kiljanczyk on 6/7/25, 5:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 6/7/25, 5:53 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.cast

import android.util.Log
import com.google.android.gms.cast.framework.CastContext
import dev.thomas_kiljanczyk.lyriccast.core.model.enums.ControlAction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CastMessagingContext(
    private val castContext: CastContext,
    private val mainDispatcher: CoroutineDispatcher
) : MessageTransport {
    companion object {
        private const val TAG = "CastMessagingContext"
        private const val CONTENT_NAMESPACE: String = "urn:x-cast:lyric.cast.content"
        private const val CONTROL_NAMESPACE: String = "urn:x-cast:lyric.cast.control"
    }

    private val _isBlanked: MutableStateFlow<Boolean> = MutableStateFlow(true)
    override val isBlanked get() = _isBlanked.asStateFlow()

    override suspend fun sendContentMessage(message: String) {
        val formattedMessage = message.replace("\n", "<br>").replace("\r", "")

        val messageContentJson = Json.encodeToString(TextCastMessage(formattedMessage))

        Log.d(TAG, "Sending content message")
        Log.d(TAG, "Namespace: $CONTENT_NAMESPACE")
        Log.d(TAG, "Content: $messageContentJson")

        return withContext(mainDispatcher) {
            val castSession =
                castContext.sessionManager.currentCastSession
            if (castSession == null) {
                Log.d(TAG, "Message not sent (no session)")
                return@withContext
            }

            castSession.sendMessage(CONTENT_NAMESPACE, messageContentJson)
        }
    }

    override suspend fun sendBlank(blanked: Boolean) {
        if (sendControlMessage(ControlAction.BLANK, blanked)) {
            _isBlanked.value = blanked
        }
    }

    override suspend fun sendConfiguration(configuration: CastConfiguration) {
        sendControlMessage(
            ControlAction.CONFIGURE, configuration
        )
    }

    override fun onSessionEnded() {
        _isBlanked.value = true
    }

    private suspend inline fun <reified T> sendControlMessage(
        action: ControlAction,
        value: T
    ): Boolean {
        val messageJson = Json.encodeToString(ControlCastMessage(action.toString(), value))

        Log.d(TAG, "Sending control message")
        Log.d(TAG, "Namespace: $CONTROL_NAMESPACE")
        Log.d(TAG, "Content: $messageJson")

        return withContext(mainDispatcher) {
            val castSession = castContext.sessionManager.currentCastSession
            if (castSession == null) {
                Log.d(TAG, "Message not sent (no session)")
                return@withContext false
            }

            castSession.sendMessage(CONTROL_NAMESPACE, messageJson)
            true
        }
    }
}
