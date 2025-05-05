//import io.quarkus.gradle.tasks.QuarkusDev
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension


val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
group = "io.brule"
version = "0.0.0"
plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.allopen") version "2.1.20"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.quarkus")
    idea
}



repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}
dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-grpc")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkiverse.mcp:quarkus-mcp-server-sse:1.1.0")
    implementation("com.google.protobuf:protobuf-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    testImplementation("io.quarkiverse.langchain4j:quarkus-langchain4j-core")
    testImplementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2-q:1.0.0-beta3")
    testImplementation("io.quarkiverse.langchain4j:quarkus-langchain4j-qdrant:0.27.0.CR1")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}
sourceSets {
    create("intTest") {
        val mainOutput = sourceSets["main"].output
        val testOutput = sourceSets["test"].output
        val testRuntime = sourceSets["test"].runtimeClasspath
        compileClasspath += mainOutput + testOutput + testRuntime
        runtimeClasspath += output + compileClasspath
    }
    create("e2eTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

configurations {
    getByName("intTestImplementation") {
        extendsFrom(configurations.testImplementation.get())
    }
    getByName("intTestRuntimeOnly") {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
    getByName("e2eTestImplementation") {
        extendsFrom(configurations.testImplementation.get())
    }
    getByName("e2eTestRuntimeOnly") {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
}

tasks.register<Test>("intTest") {
    group = "verification"
    description = "Runs integration tests"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    systemProperty("build.output.directory", "build")
    systemProperty("quarkus.profile", "intTest")
}

tasks.register<Test>("e2eTest") {
    group = "verification"
    description = "Runs e2e tests"

    val quarkusBuild = tasks.getByName("quarkusBuild")
    dependsOn(quarkusBuild)

    testClassesDirs = sourceSets["e2eTest"].output.classesDirs
    classpath = sourceSets["e2eTest"].runtimeClasspath

    systemProperty("build.output.directory", "build")
}

idea.module {
    testSources.from(sourceSets["intTest"].kotlin.srcDirs)
    testSources.from(sourceSets["e2eTest"].kotlin.srcDirs)
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
tasks.withType<Test>().configureEach {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

quarkus{
    setFinalName("memory-server")
}

tasks.processResources {
    // Exclude all .proto files from being copied to resources
    exclude("**/*.proto")
}
