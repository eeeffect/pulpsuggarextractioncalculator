import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    alias(libs.plugins.android.application)
}

kotlin {
    // Android target
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    // JVM (desktop) target
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Видаляємо конфліктуючі залежності Material3 зі спільного коду
                implementation("org.jetbrains.compose.runtime:runtime:1.5.10")
                implementation("org.jetbrains.compose.foundation:foundation:1.5.10")
                // Видаляємо material3 з commonMain, щоб уникнути конфліктів
                // implementation("org.jetbrains.compose.material3:material3:1.5.10")
                implementation("org.jetbrains.compose.ui:ui:1.5.10")
            }
        }

        // Android-специфічні залежності
        val androidMain by getting {
            dependencies {
                // Android-специфічні залежності
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.activity.compose)

                // Використовуємо явні версії Compose для Android
                implementation("androidx.compose.ui:ui:1.5.4")
                implementation("androidx.compose.ui:ui-graphics:1.5.4")
                implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
                // Використовуємо конкретну версію Material3 для Android
                implementation("androidx.compose.material3:material3:1.1.2")
            }
        }

        val desktopMain by getting {
            dependencies {
                // Desktop-специфічні залежності
                implementation("org.jetbrains.compose.desktop:desktop:1.5.10")
                // Додаємо Material3 для Desktop окремо з правильною версією
                implementation("org.jetbrains.compose.material3:material3-desktop:1.5.10")
                implementation(libs.skiko)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }
    }
}

android {
    namespace = "com.barkhatov.pulpsuggarextractioncalculator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.barkhatov.pulpsuggarextractioncalculator"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "35.0.0 rc4"
}

compose.desktop {
    application {
        mainClass = "com.barkhatov.pulpsuggarextractioncalculator.MainDesktopKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "PulpSugarCalculator"
            packageVersion = "1.0.0"

            windows {
                menuGroup = "Розрахунок вмісту цукрози"
                upgradeUuid = "61DAB35E-22CB-4FB5-A5FB-5E7FC3B0A595"
            }
        }
    }
}