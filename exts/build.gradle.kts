plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(30)
    
    defaultConfig {
        buildToolsVersion = "30.0.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
apply(from = "$projectDir/module-dependencies.gradle")