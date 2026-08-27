/*
 * Created by Tomasz Kiljanczyk on 9/6/25, 8:20 PM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/6/25, 8:16 PM
 */

package dev.thomas_kiljanczyk.lyriccast.feature.category.impl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.thomas_kiljanczyk.lyriccast.feature.category.impl.ui.CategoryManagerScreen
import kotlinx.serialization.Serializable

@Serializable
data object CategoryManagerRoute

object CategoryDeepLinks {
    const val SCHEME = "lyriccast"
    const val HOST = "app"
    const val CATEGORY_MANAGER_PATTERN = "$SCHEME://$HOST/categories"
}

fun NavController.navigateToCategoryManager(navOptions: NavOptions? = null) =
    navigate(route = CategoryManagerRoute, navOptions)

fun NavGraphBuilder.categoryManagerScreen(
    onNavigateUp: () -> Unit,
) {
    composable<CategoryManagerRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = CategoryDeepLinks.CATEGORY_MANAGER_PATTERN
            }
        ),
    ) {
        CategoryManagerScreen(
            onNavigateUp = onNavigateUp,
        )
    }
}
