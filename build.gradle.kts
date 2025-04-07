// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Видаляємо kotlin-android плагін, щоб уникнути конфлікту
    // alias(libs.plugins.kotlin.android) apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.9.0" apply false
    id("org.jetbrains.compose") version "1.5.10" apply false
}

// Переконуємося, що всі необхідні репозиторії додані
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}