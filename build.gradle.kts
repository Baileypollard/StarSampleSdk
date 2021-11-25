plugins {
    kotlin("multiplatform") version "1.5.30"
    id("com.android.library")
    id("kotlin-android-extensions")
}

group = "com.shopify"
version = "1.0-SNAPSHOT"

repositories {
    google()
    jcenter()
    mavenCentral()
}

kotlin {
    android()
    val isSim = findProperty("kotlin.device") == "iosSim"
    val iosTarget = if (isSim) iosX64("ios") else iosArm64("ios")

    iosTarget.compilations.getByName("main").cinterops.create("StarIO")
    iosTarget.compilations.getByName("main").cinterops.create("StarIO_Extension")

    iosTarget.binaries {
        framework {
            baseName = "StarSdk"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.material:material:1.2.1")
            }
        }
        val iosMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }
}