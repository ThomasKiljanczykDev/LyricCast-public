package dev.thomas_kiljanczyk.lyriccast.core.model.settings

enum class ColorOption(val value: String) {
    BLACK("Black"),
    WHITE("White"),
    BLUE("Blue"),
    RED("Red"),
    DEEP_PINK("DeepPink");

    companion object {
        fun fromValue(value: String): ColorOption? = entries.find { it.value == value }
    }
}
