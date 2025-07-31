pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")  // Android Gradle plugins
                includeGroupByRegex("com\\.google.*")  // Google-specific plugins
                includeGroupByRegex("androidx.*")      // AndroidX libraries
            }
        }
        mavenCentral()  // Central repository for Java and Android libraries
        gradlePluginPortal()  // Gradle's official plugin repository
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Enforce centralized dependency repositories
    repositories {
        google()  // Google's Maven repository (Android-specific dependencies)
        mavenCentral()  // Central repository for dependencies
        maven("https://jitpack.io")  // JitPack repository for open-source libraries
    }
}

// Set the name of the root project
rootProject.name = "Bus"

// Include the app module
include(":app")
