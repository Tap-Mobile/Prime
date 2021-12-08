//version 0.4.5

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        buildToolsVersion = "30.0.3"
        ndkVersion = "21.3.6528147"

        javaCompileOptions.annotationProcessorOptions.includeCompileClasspath = true
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
    }

    packagingOptions {
        exclude("go/*.java")
        exclude("licenses/*.txt")
        exclude("licenses/*.TXT")
        exclude("licenses/*.xml")
        exclude("META-INF/*.txt")
        exclude("META-INF/plexus/*.xml")
        exclude("org/apache/maven/project/*.xml")
        exclude("org/codehaus/plexus/*.xml")
        exclude("org/cyberneko/html/res/*.txt")
        exclude("org/cyberneko/html/res/*.properties")
        pickFirst("lib/armeabi-v7a/libgojni.so")
        pickFirst("lib/arm64-v8a/libgojni.so")
        pickFirst("lib/x86/libgojni.so")
        pickFirst("lib/x86_64/libgojni.so")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":network-domain"))
    implementation("me.proton.core:util-kotlin:0.2.4")
    implementation("me.proton.core:domain:0.2.4")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("me.proton.core:util-android-shared-preferences:0.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.21")
    implementation("commons-codec:commons-codec:1.14")
    implementation("net.gotev:cookie-store:1.3.1")
    implementation("org.minidns:minidns-hla:1.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.datatheorem.android.trustkit:trustkit:1.1.3")

    //testImplementation("retrofit2:converter-scalars:2.9.0")
    //testImplementation("okhttp3:mockwebserver:4.8.0")
    //testImplementation("me.proton.core:test-kotlin:0.2.0")
    //testImplementation("me.proton.core:test-android:0.3.3")

//    testImplementation(
//        project(Module.kotlinTest),
//        project(Module.androidTest),
//        `retrofit-scalars-converter`,
//        `mockWebServer`
//    )
}
