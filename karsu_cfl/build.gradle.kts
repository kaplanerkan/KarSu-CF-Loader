plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
    signing
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
                groupId = "com.mikhaellopez"
                artifactId = "karsu_circular_fillable_loaders"
                version = "1.5.0"

                pom {
                    name.set("CircularFillableLoaders")
                    description.set("Android library for creating circular fillable loaders with wave animation.")
                    url.set("https://github.com/lopspower/CircularFillableLoaders")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("mikhael.lopez")
                            name.set("Mikhael LOPEZ")
                            email.set("lopez.mikhael@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/lopspower/CircularFillableLoaders")
                        developerConnection.set("scm:git:https://github.com/lopspower/CircularFillableLoaders")
                        url.set("https://github.com/lopspower/CircularFillableLoaders")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "sonatype"
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = findProperty("ossrhUsername") as String? ?: ""
                    password = findProperty("ossrhPassword") as String? ?: ""
                }
            }
        }
    }

    signing {
        sign(publishing.publications["release"])
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}
