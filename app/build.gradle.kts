plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.kevin.flangejointassembly"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.kevin.flangejointassembly"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

val apkOutputDir = layout.buildDirectory.dir("outputs/apk")

tasks.register<Copy>("renameDebugApk") {
    dependsOn("assembleDebug")
    from(apkOutputDir.map { it.dir("debug") })
    include("app-debug.apk")
    rename("app-debug.apk", "FlangeHelper-debug.apk")
    into(apkOutputDir.map { it.dir("debug") })
}

tasks.register<Copy>("renameReleaseApk") {
    dependsOn("assembleRelease")
    from(apkOutputDir.map { it.dir("release") })
    include("app-release.apk")
    rename("app-release.apk", "FlangeHelper-release.apk")
    into(apkOutputDir.map { it.dir("release") })
}

tasks.named("assembleDebug").configure {
    finalizedBy("renameDebugApk")
}

tasks.named("assembleRelease").configure {
    finalizedBy("renameReleaseApk")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
