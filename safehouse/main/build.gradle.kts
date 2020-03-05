/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */



plugins {
    id("com.android.application")
    id("checkstyle")
    kotlin("android")
    kotlin("android.extensions")
}

apply {
    plugin("kotlin-android")
    plugin("kotlin-android-extensions")
    plugin("kotlin-kapt")
}

repositories {
    google()
    jcenter()
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.google.com")
}


val openvpn3SwigFiles = File(buildDir, "generated/source/ovpn3swig/ovpn3")

tasks.register<Exec>("generateOpenVPN3Swig")
{
    var swigcmd = "swig"
    // Workaround for Mac OS X since it otherwise does not find swig and I cannot get
    // the Exec task to respect the PATH environment :(
    if (File("/usr/local/bin/swig").exists())
        swigcmd = "/usr/local/bin/swig"

    doFirst {
        mkdir(openvpn3SwigFiles)
    }
    commandLine(listOf(swigcmd, "-outdir", openvpn3SwigFiles, "-outcurrentdir", "-c++", "-java", "-package", "net.openvpn.ovpn3",
            "-Isrc/main/cpp/openvpn3/client", "-Isrc/main/cpp/openvpn3/",
            "-o", "${openvpn3SwigFiles}/ovpncli_wrap.cxx", "-oh", "${openvpn3SwigFiles}/ovpncli_wrap.h",
            "src/main/cpp/openvpn3/javacli/ovpncli.i"))
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(29)  //'Q'.toInt()
        versionCode = 223
        versionName = "1.25"
        //manifestPlaceholders = ["onesignal_app_id":"282d54b3-3623-4516-937d-bec0fc1fedc4", "onesignal_google_project_number":"REMOTE"]

        externalNativeBuild {
            cmake {
                //arguments = listOf("-DANDROID_TOOLCHAIN=clang",
                //        "-DANDROID_STL=c++_static")
            }
        }
    }

    externalNativeBuild {
        cmake {
            setPath(File("${projectDir}/src/main/cpp/CMakeLists.txt"))
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "build/ovpnassets")
        }

        /*  create("ui") {
              java.srcDirs("src/ovpn3/java/", openvpn3SwigFiles)
          }
          create("skeleton") {
          }*/

        getByName("debug") {

        }

        getByName("release") {

        }
    }

    signingConfigs {
        create("release") {
            keyAlias = "almasecure"
            keyPassword = "24yxHzSby7HnLPM"
            storePassword = "MSukrBST8C7epuG"
            storeFile = file("/Users/tft/Desktop/SafeHouseAndroid/mobile/keystore_almasecure.jks")
        }
    }

    lintOptions {
        isAbortOnError = false
        enable("BackButton", "EasterEgg", "StopShip", "IconExpectedSize", "GradleDynamicVersion", "NewerVersionAvailable")
        warning("ImpliedQuantity", "MissingQuantity")
        disable("MissingTranslation", "UnsafeNativeCodeLocation")
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            // change release to debug
            // minifyEnabled false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    flavorDimensions("implementation")

    productFlavors {
        /*create("ui") {
            setDimension("implementation")
            buildConfigField("boolean", "openvpn3", "true")
        }
        create("skeleton") {
            setDimension("implementation")
            buildConfigField("boolean", "openvpn3", "false")
        }*/
        create("bodyGuard") {
            applicationId = "com.safehouse.bodyguard"
        }
    }


    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    splits {
        abi {
            setEnable(true)
            reset()
            include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
            setUniversalApk(true)

        }
    }
}

// ~/.gradle/gradle.properties
if (project.hasProperty("keystoreFile") &&
        project.hasProperty("keystorePassword") &&
        project.hasProperty("keystoreAliasPassword")) {
    android.signingConfigs.getByName("release") {
        storeFile = file(project.properties["keystoreFile"] as String)
        storePassword = project.properties["keystorePassword"] as String
        keyPassword = project.properties["keystoreAliasPassword"] as String
        keyAlias = project.properties["keystoreAlias"] as String
    }
} else {
    android.buildTypes.getByName("release").signingConfig = null
}


/* Hack-o-rama but it works good enough and documentation is surprisingly sparse */

val swigTask = tasks.named("generateOpenVPN3Swig")
val preBuildTask = tasks.getByName("preBuild")
val assembleTask = tasks.getByName("assemble")

assembleTask.dependsOn(swigTask)
preBuildTask.dependsOn(swigTask)

/* Normally you would put these on top but then it errors out on unknown configurations */
dependencies {
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.firebase:firebase-messaging:20.0.0")
    implementation("androidx.annotation:annotation:1.1.0'")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.android.support:multidex:1.0.3")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.2")
    implementation("com.contrarywind:Android-PickerView:4.1.8")
    implementation("com.android.volley:volley:1.1.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.airbnb.android:lottie:3.0.7")
    implementation("com.mixpanel.android:mixpanel-android:5.+")
    testImplementation("org.robolectric:robolectric:4.3")
    implementation("androidx.core:core-ktx:+")
    implementation("com.google.firebase:firebase-analytics:17.2.0")
    implementation("com.google.firebase:firebase-messaging:17.3.4")
    implementation(project(":textinterceptorservice"))
}

apply {
    plugin("com.google.gms.google-services")
}