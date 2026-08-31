package dev.thomas_kiljanczyk.lyriccast.core.session

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

/**
 * Pairs JSON serialization with Zstd compression in a single seam.
 *
 * Every wire payload exchanged between LyricCast nodes (Cast receiver and Nearby peers)
 * is produced by [encode] and consumed by [decode]. Implementations are expected to
 * tolerate uncompressed input on the decode side so older or fallback senders remain
 * interoperable.
 */
interface SessionMessageCodec {
    suspend fun <T : Any> encode(value: T, serializer: KSerializer<T>): ByteArray

    suspend fun <T : Any> decode(bytes: ByteArray, deserializer: KSerializer<T>): T
}

suspend inline fun <reified T : Any> SessionMessageCodec.encode(value: T): ByteArray =
    encode(value, serializer())

suspend inline fun <reified T : Any> SessionMessageCodec.decode(bytes: ByteArray): T =
    decode(bytes, serializer())

suspend inline fun <reified T : Any> SessionMessageCodec.decodeOrNull(bytes: ByteArray): T? =
    try {
        decode(bytes)
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
