
package dev.thomas_kiljanczyk.lyriccast.feature.session.impl.model

data class GmsNearbySessionItem(
    val deviceName: String,
    val endpointId: String
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is GmsNearbySessionItem) {
            return false
        }

        return endpointId == other.endpointId
    }

    override fun hashCode(): Int {
        return endpointId.hashCode()
    }
}
