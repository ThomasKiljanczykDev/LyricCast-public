/*
 * Created by Tomasz Kiljanczyk on 6/7/26, 7:21 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 6/7/26, 7:18 PM
 */

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
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun acceptConnection(p0: String, p1: PayloadCallback): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun cancelPayload(p0: Long): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun rejectConnection(p0: String): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: String,
        p1: String,
        p2: ConnectionLifecycleCallback
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: ByteArray,
        p1: String,
        p2: ConnectionLifecycleCallback
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: String,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: ConnectionOptions
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun requestConnection(
        p0: ByteArray,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: ConnectionOptions
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun sendPayload(p0: String, p1: Payload): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun sendPayload(p0: MutableList<String>, p1: Payload): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun startAdvertising(
        p0: String,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: AdvertisingOptions
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun startAdvertising(
        p0: ByteArray,
        p1: String,
        p2: ConnectionLifecycleCallback,
        p3: AdvertisingOptions
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun startDiscovery(
        p0: String,
        p1: EndpointDiscoveryCallback,
        p2: DiscoveryOptions
    ): Task<Void> {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun disconnectFromEndpoint(p0: String) {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun stopAdvertising() {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun stopAllEndpoints() {
        // Does not take part in tests
        throw NotImplementedError()
    }

    override fun stopDiscovery() {
        // Does not take part in tests
        throw NotImplementedError()
    }
}
