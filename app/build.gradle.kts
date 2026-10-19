import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val gitVersionProvider = providers.exec {
    commandLine("git", "describe", "--tags", "--always", "--dirty")
    isIgnoreExitValue = true
}.standardOutput.asText.map { out ->
    val tag = out.trim().removePrefix("v")
    if (tag.isEmpty()) "1.0.0-dev" else tag
}.orElse("1.0.0-dev")

fun calculateVersionCode(versionName: String): Int {
    val clean = versionName.split("-")[0].removePrefix("v")
    val parts = clean.split(".")
    val major = parts.getOrNull(0)?.toIntOrNull() ?: 1
    val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
    return major * 10000 + minor * 100 + patch
}

val keystorePropertiesFile = rootProject.file("secrets.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.laralbir.mdghnavigator"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.laralbir.mdghnavigator"
        minSdk = 24
        targetSdk = 36

        val vName = gitVersionProvider.get()
        versionName = vName
        versionCode = calculateVersionCode(vName)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val ksPath = System.getenv("KEYSTORE_PATH")
                ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_PATH")
            val customKeystore = if (ksPath != null) file(ksPath) else null

            if (customKeystore != null && customKeystore.exists()) {
                storeFile = customKeystore
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                    ?: keystoreProperties.getProperty("RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                    ?: keystoreProperties.getProperty("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                    ?: keystoreProperties.getProperty("RELEASE_KEY_PASSWORD")
            } else {
                val standaloneKey = rootProject.file("keystore/release-standalone.jks")
                if (standaloneKey.exists()) {
                    storeFile = standaloneKey
                    storePassword = "mdghnavigator"
                    keyAlias = "release"
                    keyPassword = "mdghnavigator"
                } else {
                    throw GradleException("Release keystore not found at ${standaloneKey.absolutePath}. A valid signing keystore is required for release builds.")
                }
            }
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}


kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:renderer"))

    implementation(project(":feature:library"))
    implementation(project(":feature:reader"))
    implementation(project(":feature:navigator"))
    implementation(project(":feature:annotations"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:tts"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

abstract class GenerateChecksumsTask : DefaultTask() {
    @get:Internal
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val dir = outputDir.orNull?.asFile ?: return
        if (!dir.exists()) return
        val releaseFiles = dir.walkTopDown().filter { file ->
            (file.name == "app-release.apk" || file.name == "app-release.aab") &&
            !file.name.endsWith(".sha256")
        }.toList()

        releaseFiles.forEach { file ->
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hash = file.readBytes().let { bytes: ByteArray ->
                messageDigest.digest(bytes).joinToString("") { b -> "%02x".format(b) }
            }
            val checksumFile = File(file.parentFile, "${file.name}.sha256")
            checksumFile.writeText("$hash  ${file.name}\n")
            println("SHA-256 [${file.name}]: $hash")

            // Also produce friendly release artifact copy if APK
            if (file.name == "app-release.apk") {
                val friendlyName = "md-gh-navigator-universal-release.apk"
                val friendlyFile = File(file.parentFile, friendlyName)
                file.copyTo(friendlyFile, overwrite = true)
                val friendlyChecksum = File(file.parentFile, "$friendlyName.sha256")
                friendlyChecksum.writeText("$hash  $friendlyName\n")
                println("Generated friendly release asset: $friendlyName (SHA-256: $hash)")
            }
        }
    }
}

tasks.register<GenerateChecksumsTask>("generateReleaseChecksums") {
    group = "distribution"
    description = "Computes SHA-256 checksums for built release APKs and AABs"
    dependsOn("assembleRelease", "bundleRelease")
    outputDir.set(layout.buildDirectory.dir("outputs"))
}
