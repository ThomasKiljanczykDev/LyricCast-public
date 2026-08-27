/*
 * Created by Tomasz Kiljanczyk on 25/01/2025, 18:55
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 10/01/2025, 01:46
 */

package dev.thomas_kiljanczyk.lyriccast.core.nearby

import java.util.UUID

object GmsNearbyConstants {
    /** Live casting-session protocol between a session server and its clients. */
    val CAST_SESSION_SERVICE_UUID: UUID = UUID.fromString("2f58e6c0-5ccf-4d2f-afec-65a2d98e2141")

    /** Import/export data-transfer protocol between two devices. */
    val IMPORT_SESSION_SERVICE_UUID: UUID = UUID.fromString("43f85e14-51be-4a65-87b1-b95203ab9ef2")
}
