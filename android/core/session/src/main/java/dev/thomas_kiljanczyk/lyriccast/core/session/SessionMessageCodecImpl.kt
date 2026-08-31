package dev.thomas_kiljanczyk.lyriccast.core.session

import javax.inject.Inject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class SessionMessageCodecImpl @Inject constructor(
    private val zstdCompression: ZstdCompressionUtil
) : SessionMessageCodec {

    @OptIn(ExperimentalSerializationApi::class)
    private val json: Json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun <T : Any> encode(value: T, serializer: KSerializer<T>): ByteArray {
        val jsonString = json.encodeToString(serializer, value)
        return zstdCompression.compress(jsonString) ?: jsonString.toByteArray(Charsets.UTF_8)
    }

    override suspend fun <T : Any> decode(bytes: ByteArray, deserializer: KSerializer<T>): T {
        val jsonString = zstdCompression.decompressToString(bytes) ?: bytes.decodeToString()
        return json.decodeFromString(deserializer, jsonString)
    }
}
