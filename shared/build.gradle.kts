import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget



plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}





kotlin {
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    androidLibrary {
       namespace = "com.example.AdminPanel.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.materialIconsCore)
            implementation(libs.kotlinx.serialization.json)

            //Ktor!
            implementation("io.ktor:ktor-client-core:3.1.3")

            implementation("io.ktor:ktor-client-cio:3.1.3")

            implementation("io.ktor:ktor-client-content-negotiation:3.1.3")

            implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.3")


            implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")


            // Coil core for Compose Multiplatform
            implementation("io.coil-kt.coil3:coil-compose:3.5.0")

            // Network engine to fetch URLs (Ktor works great on all platforms)
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.5.0")

            implementation("io.github.vinceglb:filekit-compose:0.8.8")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }

    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}