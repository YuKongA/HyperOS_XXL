@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {

        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://api.xposed.info/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://api.xposed.info/")
    }
}

rootProject.name = ("HyperOS_XXL")
include(":app", ":blockmiui")
