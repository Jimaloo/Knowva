import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.21"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Android-specific DI
            implementation("io.insert-koin:koin-android:3.5.3")
            implementation("io.insert-koin:koin-androidx-compose:3.5.3")

            // Android-specific networking
            implementation("io.ktor:ktor-client-okhttp:2.3.7")
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.animation)
            implementation(compose.animationGraphics)

            // Lifecycle & ViewModel
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")

            // Dependency Injection - Koin
            implementation("io.insert-koin:koin-core:3.5.3")
            implementation("io.insert-koin:koin-compose:1.1.2")

            // Networking - Ktor
            implementation("io.ktor:ktor-client-core:2.3.7")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            implementation("io.ktor:ktor-client-logging:2.3.7")
            implementation("io.ktor:ktor-client-auth:2.3.7")

            // Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

            // Data Storage
            implementation("io.github.xxfast:kstore:0.8.0")
            implementation("io.github.xxfast:kstore-file:0.8.0")

            // Image Loading
            implementation("io.github.qdsfdhvh:image-loader:1.7.1")

            // UUID Generation
            implementation("com.benasher44:uuid:0.8.2")

            // Logging
            implementation("io.github.aakira:napier:2.7.1")

            // Shared module
            implementation(projects.shared)
        }

        iosMain.dependencies {
            // iOS-specific networking
            implementation("io.ktor:ktor-client-darwin:2.3.7")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            implementation("io.insert-koin:koin-test:3.5.3")
        }
    }
}

android {
    namespace = "com.knowva.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.knowva.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

