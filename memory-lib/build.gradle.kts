import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// memory-lib/build.gradle.kts

plugins {
    // Kotlin JVM for shared library
    kotlin("jvm")
    // Serialization support if needed for DTOs
    kotlin("plugin.serialization")
    id("org.kordamp.gradle.jandex")
}

// Align group and version with parent
group = "io.brule.memory"
version = "0.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Expose DTOs and utilities
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Jackson Kotlin for JSON serialization in CLI and server
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    // PicoCLI annotations (for memory-cli)
    api("info.picocli:picocli:4.7.5")

    // Logging abstraction
    api("org.jboss.logging:jboss-logging:3.5.0.Final")

    // JAX-RS Annotations
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")

    // Test dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
extensions.configure<KotlinJvmProjectExtension> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        javaParameters = true
    }
}