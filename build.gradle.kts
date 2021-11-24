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

    iosArm64("ios") {
        compilations.getByName("main").cinterops.create("StarIO")
        compilations.getByName("main").cinterops.create("StarIO_Extension")

        binaries {
            framework {
                baseName = "StarSdk"
            }
        }
    }


    sourceSets {
        val commonMain by getting
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