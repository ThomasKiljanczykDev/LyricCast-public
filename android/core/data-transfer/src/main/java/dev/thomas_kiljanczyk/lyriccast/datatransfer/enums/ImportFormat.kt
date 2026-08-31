package dev.thomas_kiljanczyk.lyriccast.datatransfer.enums

enum class ImportFormat(val displayName: String) {
    NONE("NONE"),
    OPEN_SONG("OpenSong"),
    LYRIC_CAST("LyricCast");

    companion object {
        fun getByName(name: String): ImportFormat {
            return entries.first { it.displayName == name }
        }
    }
}
