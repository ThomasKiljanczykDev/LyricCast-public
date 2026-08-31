package dev.thomas_kiljanczyk.lyriccast.core.model.settings

enum class ThemeOption(val value: Int) {
    LIGHT(1),
    DARK(2),
    SYSTEM(-1);

    companion object {
        fun fromValue(value: Int): ThemeOption? = entries.find { it.value == value }
    }
}
