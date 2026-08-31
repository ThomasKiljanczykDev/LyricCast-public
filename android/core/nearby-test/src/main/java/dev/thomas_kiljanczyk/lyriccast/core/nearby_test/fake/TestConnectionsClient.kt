package dev.thomas_kiljanczyk.lyriccast.core.nearby_test.fake

import com.google.android.gms.common.api.internal.ApiKey
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionOptions
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.tasks.Task

/**
 * Test implementation of [ConnectionsClient] for testing.
 * All methods throw [NotImplementedError] by default - override specific methods as needed.
 */
class TestConnectionsClient : ConnectionsClient {
    override fun getApiKey(): ApiKey<ConnectionsOptions> {
        throw NotImplementedError()
    }

    override fun acceptConnection(p0: String, p1: PayloadCallback): Task<Void> {
        throw NotImplementedError()
    }

    override fun cancelPayload(p0: Long): Task<Void> {
        throw NotImplementedError()
    }

    override fun rejectConnection(p0: String): Task<Void> {
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: String,
        p1: String,
        p2: ConnectionLifecycleCallback
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: ByteArray,
        p1: String,
        p2: ConnectionLifecycleCallback
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: String,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: ConnectionOptions
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: ByteArray,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: ConnectionOptions
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun sendPayload(p0: String, p1: Payload): Task<Void> {
        throw NotImplementedError()
    }

    override fun sendPayload(p0: MutableList<String>, p1: Payload): Task<Void> {
        throw NotImplementedError()
    }

    override fun startAdvertising(
        p0: String,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: AdvertisingOptions
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun startAdvertising(
        p0: ByteArray,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: AdvertisingOptions
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun startDiscovery(
        p0: String,
        p1: EndpointDiscoveryCallback,
        p2: DiscoveryOptions
    ): Task<Void> {
        throw NotImplementedError()
    }

    override fun disconnectFromEndpoint(p0: String) {
        throw NotImplementedError()
    }

    override fun stopAdvertising() {
        throw NotImplementedError()
    }

    override fun stopAllEndpoints() {
        throw NotImplementedError()
    }

    override fun stopDiscovery() {
        throw NotImplementedError()
    }
}
