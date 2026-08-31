package dev.thomas_kiljanczyk.lyriccast.core.testing

import androidx.compose.ui.test.junit4.v2.createComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

abstract class BaseComposeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    open fun setup() {
        hiltRule.inject()
    }
}
