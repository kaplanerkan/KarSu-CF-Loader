plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.karsu.cfl"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.kaplanerkan"
                artifactId = "karsu_cf_loaders"
                version = "2.0.0"

                pom {
                    name.set("KarSu CF Loaders")
                    description.set("Android library for creating circular fillable loaders with wave animation and text overlay.")
                    url.set("https://github.com/kaplanerkan/KarSu-CF-Loader")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("kaplanerkan")
                            name.set("Erkan Kaplan")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/kaplanerkan/KarSu-CF-Loader")
                        developerConnection.set("scm:git:https://github.com/kaplanerkan/KarSu-CF-Loader")
                        url.set("https://github.com/kaplanerkan/KarSu-CF-Loader")
                    }
                }
            }
        }

    }
}

dependencies {
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.espresso.core)
}
