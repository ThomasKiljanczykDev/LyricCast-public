/*
 * Created by Tomasz Kiljanczyk on 9/7/25, 8:53 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 8:09 PM
 */

package dev.thomas_kiljanczyk.lyriccast.shared

import androidx.compose.ui.test.junit4.createComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule

/**
 * Base class for Compose UI tests with Hilt support.
 * Provides common setup and rules for Compose testing.
 */
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