import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.ByteArrayOutputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.yuk.HyperOS_XXL"
    compileSdk = 34

    defaultConfig {
        applicationId = namespace
        minSdk = 34
        targetSdk = 34
        versionCode = getVersionCode()
        versionName = "1.0" + "-" + getVersionName()
        //noinspection ChromeOsAbiSupport
        ndk.abiFilters += arrayOf("arm64-v8a")
    }
    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    if (keystorePath != null) {
        signingConfigs {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
            }
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro", "proguard-log.pro")
            if (keystorePath != null) signingConfig = signingConfigs.getByName("release")
        }
        debug {
            if (keystorePath != null) signingConfig = signingConfigs.getByName("release")
        }
    }
    androidResources {
        additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x52")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "**"
        }
        applicationVariants.all {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName = "HyperOS_XXL-$versionName($versionCode)-$name.apk"
            }
        }
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

fun getGitCommitCount(): Int {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-list", "--count", "HEAD")
        standardOutput = out
    }
    return out.toString().trim().toInt()
}

fun getGitDescribe(): String {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "describe", "--tags", "--always")
        standardOutput = out
    }
    return out.toString().trim()
}

fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    val major = 5
    return major + commitCount
}

fun getVersionName(): String {
    return getGitDescribe()
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

dependencies {
    compileOnly(libs.xposed.api)
    implementation(project(":blockmiui"))
    implementation(libs.ezXHelper)
    implementation(libs.dexKit)
}
