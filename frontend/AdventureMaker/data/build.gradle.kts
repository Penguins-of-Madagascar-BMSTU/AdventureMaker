plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val apiKey2GIS = property("apiKey2GIS")?.toString() ?:
        error("No 2GIS api key defined in gradle.properties.")
        buildConfigField("String", "API_KEY_2GIS", "\"$apiKey2GIS\"")

        val s3AccessKey = property("s3AccessKey")?.toString() ?:
        error("No s3AccessKey api key defined in gradle.properties.")
        buildConfigField("String", "S3_ACCESS_KEY", "\"$s3AccessKey\"")

        val s3SecretKey = property("s3SecretKey")?.toString() ?:
        error("No s3AccessKey api key defined in gradle.properties.")
        buildConfigField("String", "S3_SECRET_KEY", "\"$s3SecretKey\"")

        val s3Region = property("s3Region")?.toString() ?:
        error("No s3AccessKey api key defined in gradle.properties.")
        buildConfigField("String", "S3_REGION", "\"$s3Region\"")

        val s3Endpoint = property("s3Endpoint")?.toString() ?:
        error("No s3AccessKey api key defined in gradle.properties.")
        buildConfigField("String", "S3_ENDPOINT", "\"$s3Endpoint\"")

        val bucketName = property("bucketName")?.toString() ?:
        error("No s3AccessKey api key defined in gradle.properties.")
        buildConfigField("String", "BUCKET_NAME", "\"$bucketName\"")
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    api(libs.firebase.auth)
    api(libs.firebase.database)

    implementation(libs.datastore)
    implementation(libs.gson)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    implementation(libs.aws.android.sdk.s3)
    implementation(libs.aws.android.sdk.core)
    implementation(libs.android.maps.utils)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":domain"))
}