import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

repositories{
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.21" apply false
    kotlin("plugin.allopen") version "2.0.21" apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
    kotlin("kapt") version "2.0.21" apply false
    id("org.kordamp.gradle.jandex") version "1.0.0" apply false
//    id("io.quarkus") version "3.21.4" apply false

}
val configureJavaSettings: JavaPluginExtension.() -> Unit = {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val configureKotlinCompilerOptions: KotlinJvmProjectExtension.() -> Unit = {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        javaParameters = true
    }
}

// Java compatibility
configure<JavaPluginExtension>(configureJavaSettings)

// Kotlin compiler settings
extensions.configure<KotlinJvmProjectExtension>(configureKotlinCompilerOptions)