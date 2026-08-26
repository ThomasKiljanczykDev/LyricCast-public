/*
 * Convention plugin that adds only the Kotlin quality gate (detekt).
 */

import dev.thomas_kiljanczyk.lyriccast.buildlogic.configureDetekt
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin for modules that apply no other `lyriccast.*` plugin
 * but still need the detekt gate.
 */
class KotlinQualityConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureDetekt()
    }
}
