plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.domain"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

// 👇 Настройки тестов вынесены на корневой уровень
tasks.withType<Test> {
    useJUnit()

    // Передаём ключи из Gradle-свойств в JVM-тесты
    systemProperty("apiKey2GIS", findProperty("apiKey2GIS")?.toString() ?: "")
    systemProperty("s3AccessKey", findProperty("s3AccessKey")?.toString() ?: "")
    systemProperty("s3SecretKey", findProperty("s3SecretKey")?.toString() ?: "")
    systemProperty("s3Region", findProperty("s3Region")?.toString() ?: "")
    systemProperty("s3Endpoint", findProperty("s3Endpoint")?.toString() ?: "")
    systemProperty("bucketName", findProperty("bucketName")?.toString() ?: "")

    // Фикс для MockK на новых JDK
    jvmArgs("-Dnet.bytebuddy.experimental=true")

    // Вывод результатов в консоль
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}