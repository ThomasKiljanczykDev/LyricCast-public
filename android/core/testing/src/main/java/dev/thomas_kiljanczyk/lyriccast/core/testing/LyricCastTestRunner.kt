package dev.thomas_kiljanczyk.lyriccast.core.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * A custom [AndroidJUnitRunner] that uses [HiltTestApplication] for Hilt-based tests.
 * Configure this runner in a module's build.gradle.kts:
 * ```
 * testInstrumentationRunner = "dev.thomas_kiljanczyk.lyriccast.core.testing.LyricCastTestRunner"
 * ```
 */
@Suppress("unused")
class LyricCastTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    override fun callApplicationOnCreate(app: Application) {
        super.callApplicationOnCreate(app)
    }
}
