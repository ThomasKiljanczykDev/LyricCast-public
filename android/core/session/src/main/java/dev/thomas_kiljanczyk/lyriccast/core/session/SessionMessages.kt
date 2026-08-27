/*
 * Created by Tomasz Kiljanczyk on 25/01/2025, 18:55
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 25/01/2025, 18:55
 */

package dev.thomas_kiljanczyk.lyriccast.core.session

import kotlinx.serialization.Serializable

/**
 * Data class for received payload from a client.
 * The payload is the raw bytes as received over the wire; decode it via
 * [SessionMessageCodec] to obtain a typed message.
 */
data class ReceivedPayload(
    val endpointId: String,
    val payload: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ReceivedPayload
        if (endpointId != other.endpointId) return false
        if (!payload.contentEquals(other.payload)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = endpointId.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}

/**
 * The slide content shown to Cast and Nearby session clients: the current slide's text plus
 * enough song metadata to render a header/progress indicator on the receiving side.
 *
 * [setlist] is null in single-song mode. Optional on the wire (`ignoreUnknownKeys`) so old/new
 * clients stay compatible.
 */
@Serializable
data class ShowLyricsContent(
    val songTitle: String,
    val slideText: String,
    val slideNumber: Int,
    val totalSlides: Int,
    val setlist: SetlistContent? = null
)

/**
 * The setlist a session server is presenting from, so clients can show the running order and
 * where the presenter currently is in it.
 */
@Serializable
data class SetlistContent(
    val songs: List<SetlistSongContent>,
    val currentSongIndex: Int
)

/**
 * Identity and display title only — clients render the running order read-only and never need
 * the lyrics of songs other than the one currently on screen.
 */
@Serializable
data class SetlistSongContent(
    val id: String,
    val title: String
)
