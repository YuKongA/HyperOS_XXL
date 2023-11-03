enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://api.xposed.info/")
    }
}

plugins {
    id("com.highcapable.sweetdependency") version "1.0.3"
}

rootProject.name = ("HyperOS_XXL")
include(":app", ":blockmiui")
