plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.logilearnapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.logilearnapp"
        minSdk = 24
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    //implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //esto no estaba creo
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.annotation:annotation:1.7.1")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //base de datos
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-database-ktx")


    //implementation("com.google.firebase:firebase-firestore:24.10.0")
    //implementation(libs.androidx.compose.material3)
    implementation("com.google.accompanist:accompanist-adaptive:0.26.2-beta")
    // https://mvnrepository.com/artifact/com.google.android.material/material parece que es el que tiene más usos
   // implementation("com.google.android.material:material:1.11.0")
   implementation("com.google.android.material:material:1.10.0")

    // Fragment (de momento no usados)
    /*
    *  implementation ("androidx.fragment:fragment-ktx:1.6.2")
    // Activity
    implementation ("androidx.activity:activity-ktx:1.8.2")
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    //binding
    implementation ("androidx.databinding:databinding-runtime:8.2.0")
    //procesador de anotaciones con data binding
    annotationProcessor ("com.android.databinding:compiler:7.0.2")
    *
    * */
    //diseño de la UI de firebase para inicio de sesión
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")


     //firebase
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")

}