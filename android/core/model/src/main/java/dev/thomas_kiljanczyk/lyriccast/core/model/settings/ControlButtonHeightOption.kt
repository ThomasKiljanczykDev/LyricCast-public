package dev.thomas_kiljanczyk.lyriccast.core.model.settings

enum class ControlButtonHeightOption(val value: Int) {
    SMALL(88),
    MEDIUM(104),
    LARGE(128);

    companion object {
        fun fromValue(value: Int): ControlButtonHeightOption? = entries.find { it.value == value }
        val DEFAULT = SMALL
    }
}
