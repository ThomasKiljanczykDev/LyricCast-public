/*
 * Created by Tomasz Kiljanczyk on 4/5/21 1:02 AM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 4/5/21 12:21 AM
 */

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LyricCast"
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:ui")
include(":core:datastore-proto")
include(":core:database")
include(":core:data")
include(":core:domain")
include(":core:data-transfer")
include(":core:testing")
include(":core:data-test")
include(":core:session")
include(":core:nearby")
include(":core:nearby-test")
include(":core:cast")
include(":core:cast-test")
include(":core:playback")
include(":core:sync")
include(":core:tutorial")
include(":feature:category:impl")
include(":feature:main:impl")
include(":feature:session:impl")
include(":feature:setlist:impl")
include(":feature:settings:impl")
include(":feature:song:impl")
include(":app")
