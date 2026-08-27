/*
 * Created by Tomasz Kiljanczyk on 1/29/26, 3:45 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 1/29/26, 2:05 PM
 */

package dev.thomas_kiljanczyk.lyriccast.core.nearby

/**
 * Exception thrown when a sync operation times out waiting for a connection.
 */
class SyncTimeoutException(message: String) : Exception(message)
