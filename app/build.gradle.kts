plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")


}


android {
    namespace = "de.syntax.androidabschluss"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.syntax.androidabschluss"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {


    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.camera:camera-view:1.3.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")


    //Retrofit und Moshi
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")


    //logging for API
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    

    // GPS
    implementation ("com.google.android.gms:play-services-location:21.2.0")



    // swiper refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")



    // Coil
    implementation("io.coil-kt:coil:2.5.0")


    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")



    // Firebase
    // BOM (Bill of materials) welche uns erlaubt, alle Versionen der dependencies über diese bom zu managen.
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))



    // Authentication dependencies ohne Version
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore")


    //MPAndroidChart for charts, diagrams etc.
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


    //Google Play Services for GoogleMap
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.maps.android:maps-utils-ktx:5.0.0")


    // ML Kit Vision Dependency, For text recognition
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")


    }

