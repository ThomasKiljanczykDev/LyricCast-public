package dev.thomas_kiljanczyk.lyriccast.core.nearby

import com.google.android.gms.nearby.connection.Strategy

data class TransportConfig(
    val serviceId: String,
    val strategy: Strategy
) {
    companion object {
        val Session: TransportConfig = TransportConfig(
            serviceId = GmsNearbyConstants.CAST_SESSION_SERVICE_UUID.toString(),
            strategy = Strategy.P2P_STAR
        )

        val Sync: TransportConfig = TransportConfig(
            serviceId = GmsNearbyConstants.IMPORT_SESSION_SERVICE_UUID.toString(),
            strategy = Strategy.P2P_POINT_TO_POINT
        )
    }
}
