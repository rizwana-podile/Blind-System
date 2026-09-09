pluginManagement {
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SightGuide"

// Core Modules
include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:accessibility")
include(":core:audio")
include(":core:location")
include(":core:sensors")
include(":core:permissions")
include(":core:storage")
include(":core:security")
include(":core:logging")

// Feature Modules
include(":feature:dashboard")
include(":feature:navigation")
include(":feature:camera")
include(":feature:reader")
include(":feature:nearby")
include(":feature:safety")
include(":feature:voice")
include(":feature:caregiver")
include(":feature:settings")
