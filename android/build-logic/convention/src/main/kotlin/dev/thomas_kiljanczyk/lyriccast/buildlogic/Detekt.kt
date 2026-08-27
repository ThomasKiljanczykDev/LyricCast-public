/*
 * Shared detekt (static analysis + ktlint formatting) setup for every LyricCast module.
 */

package dev.thomas_kiljanczyk.lyriccast.buildlogic

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * Applies and configures detekt for [this] project.
 *
 * detekt is the single Kotlin static-analysis *and* formatting gate: the ktlint rules come from
 * detekt's own ktlint wrapper rule set (`dev.detekt:detekt-rules-ktlint-wrapper`), so there is no
 * standalone ktlint or Spotless plugin in this build.
 */
internal fun Project.configureDetekt() {
    pluginManager.apply("dev.detekt")

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    extensions.configure<DetektExtension> {
        parallel.set(true)
        buildUponDefaultConfig.set(true)
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        basePath.set(rootProject.layout.projectDirectory)
        ignoreFailures.set(false)
        // NOTE: type resolution (`Detekt.classpath`) is deliberately NOT configured. Enabling it
        // forces every Android variant to compile before detekt can run, which turns a ~10 s check
        // into a full build. The handful of rules that need it simply do not fire; see
        // config/detekt/detekt.yml for which ones and why that is accepted.
        // NOTE: no baseline file is configured on purpose - findings are tuned away in the config
        // or fixed in source, never frozen.
    }

    dependencies {
        add("detektPlugins", libs.findLibrary("detekt-formatting").get())
        add("detektPlugins", libs.findLibrary("detekt-composeRules").get())
    }

    // `./gradlew detekt -PdetektAutoCorrect=true` rewrites the auto-fixable (ktlint) findings in
    // place. The gate itself always runs in check-only mode.
    val autoCorrectEnabled = providers.gradleProperty("detektAutoCorrect")
        .map(String::toBoolean)
        .orElse(false)

    tasks.withType<Detekt>().configureEach {
        jvmTarget.set("17")
        autoCorrect.set(autoCorrectEnabled)
        reports {
            sarif.required.set(true)
            html.required.set(true)
            checkstyle.required.set(false)
            markdown.required.set(false)
        }
    }

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget.set("17")
    }
}
