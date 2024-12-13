plugins {
    // Menggunakan plugin Android untuk aplikasi
    id("com.android.application")
    // Menambahkan plugin Kotlin untuk mendukung pengembangan dengan Kotlin
    id("org.jetbrains.kotlin.android")
}

android {
    // Namespace aplikasi, digunakan sebagai identifier unik di proyek
    namespace = "com.dicoding.capstoneprojek"
    // Versi SDK maksimum yang didukung untuk build aplikasi
    compileSdk = 34

    defaultConfig {
        // ID aplikasi unik untuk mengidentifikasi aplikasi di perangkat dan Play Store
        applicationId = "com.dicoding.capstoneprojek"
        // Versi minimum SDK Android yang dapat menjalankan aplikasi
        minSdk = 26
        // Versi SDK yang digunakan untuk mengoptimalkan aplikasi
        targetSdk = 34
        // Versi kode aplikasi (untuk identifikasi build di Play Store)
        versionCode = 1
        // Versi nama aplikasi yang terlihat oleh pengguna
        versionName = "1.0"
        // Runner default untuk pengujian instrumentasi
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Mengatur apakah kode aplikasi akan dimampatkan dan dioptimalkan pada mode rilis
            isMinifyEnabled = false
            // File konfigurasi ProGuard untuk optimasi dan keamanan kode pada mode rilis
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Menentukan versi Java yang kompatibel dengan kode sumber
        sourceCompatibility = JavaVersion.VERSION_1_8
        // Menentukan versi Java target untuk kode yang dihasilkan
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        // Menentukan versi JVM target untuk Kotlin
        jvmTarget = "1.8"
    }
    buildFeatures {
        // Mengaktifkan fitur ViewBinding untuk mempermudah pengelolaan view
        viewBinding = true
        // Mengaktifkan buildConfig untuk menyimpan konstanta build
        buildConfig = true
        mlModelBinding = true

    }
}

dependencies {
    // **Core Android**
    // Perpustakaan inti Android yang diperluas untuk mendukung API Kotlin
    implementation("androidx.core:core-ktx:1.12.0")
    // Dukungan komponen UI untuk aplikasi Android
    implementation("androidx.appcompat:appcompat:1.6.1")
    // Komponen desain material untuk membangun UI modern
    implementation("com.google.android.material:material:1.9.0")
    // Dukungan aktivitas berbasis Kotlin
    implementation("androidx.activity:activity-ktx:1.8.0")
    // Library ConstraintLayout untuk tata letak UI yang fleksibel
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // **Lifecycle & DataStore**
    // DataStore untuk penyimpanan data kunci-nilai
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // ViewModel untuk mengelola data UI secara reaktif
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    // LiveData untuk data yang dapat diamati
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    // Komponen lifecycle runtime
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // **Networking**
    // Retrofit untuk melakukan HTTP request
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Konverter Gson untuk parsing JSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Logging interceptor untuk debugging HTTP request
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // **UI Components**
    // RecyclerView untuk membuat daftar UI yang dapat digulir
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    // ExifInterface untuk mengelola metadata gambar
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    // Glide untuk memuat dan mengelola gambar
    implementation("com.github.bumptech.glide:glide:4.15.1")
    // Aktivitas tambahan dari library dependencies
    implementation(libs.androidx.activity)
    implementation(libs.androidx.junit.ktx)

    //crop
    implementation("com.github.yalantis:ucrop:2.2.9")
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)

    // TODO: Tambahkan Library TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation(libs.play.services.basement)

    implementation ("com.google.code.gson:gson:2.8.9")


}
