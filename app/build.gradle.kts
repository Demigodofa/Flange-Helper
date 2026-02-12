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
val apkRenamedDir = layout.buildDirectory.dir("outputs/apk-renamed")

tasks.register<Copy>("renameDebugApk") {
    from(apkOutputDir.map { it.dir("debug") })
    include("app-debug.apk")
    rename("app-debug.apk", "FlangeHelper-debug.apk")
    into(apkRenamedDir.map { it.dir("debug") })
}

tasks.register<Copy>("renameReleaseApk") {
    from(apkOutputDir.map { it.dir("release") })
    include("app-release.apk")
    rename("app-release.apk", "FlangeHelper-release.apk")
    into(apkRenamedDir.map { it.dir("release") })
}

afterEvaluate {
    tasks.findByName("assembleDebug")?.finalizedBy("renameDebugApk")
    tasks.findByName("assembleRelease")?.finalizedBy("renameReleaseApk")
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
    testImplementation("org.json:json:20240303")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
