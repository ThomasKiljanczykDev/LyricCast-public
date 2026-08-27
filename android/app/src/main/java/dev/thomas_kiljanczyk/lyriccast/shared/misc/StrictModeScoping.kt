/*
 * Created by Tomasz Kiljanczyk on 8/26/26, 1:43 PM
 * Copyright (c) 2026 . All rights reserved.
 * Last modified 8/26/26, 1:43 PM
 */

package dev.thomas_kiljanczyk.lyriccast.shared.misc

import android.os.StrictMode

/**
 * Runs [block] with StrictMode's disk-read detection suspended, restoring the previous thread policy
 * afterwards.
 *
 * The platform's [StrictMode.allowThreadDiskReads] hands back the old policy and leaves restoring it
 * to the caller; this wraps that in a `try`/`finally` so a throwing [block] cannot leak a relaxed
 * policy into the rest of the process.
 *
 * Only for reads that genuinely cannot leave the main thread. Every call site must say why in a
 * comment - this is a scoping tool, not a way to silence the gate.
 */
internal inline fun <T> allowingThreadDiskReads(block: () -> T): T {
    val previousPolicy = StrictMode.allowThreadDiskReads()
    try {
        return block()
    } finally {
        StrictMode.setThreadPolicy(previousPolicy)
    }
}
