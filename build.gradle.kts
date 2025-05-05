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
//    id("org.kordamp.gradle.jandex") version "1.0.0"
//    id("com.google.protobuf") version "0.9.5"
}

// -- disable jandex indexing for kotlinx-serialization serializer classes --
//tasks.named("jandex") {
//    // turn off Jandex for our Kotlin-serialization generated serializers
//    enabled = false
//}

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
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
val protobufVersion = "3.25.5"
val grpcVersion = "1.3.0"
//protobuf {
//    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
//
//    plugins {
//        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
//    }
//
//    generateProtoTasks {
//        ofSourceSet("main").forEach { task ->
//            task.plugins {
//                id("grpc") // generates the gRPC stubs
//            }
//
//            task.builtins{
//                java{
//                    option("java_multiple_files=true")
//                }
//                id("kotlin")
//            }
//        }
//    }
//}


quarkus{

//    quarkusBuildProperties.put("quarkus.grpc.codegen.proto-directory", "${project.projectDir}/src/main/proto")
    setFinalName("memory-server")
}

tasks.processResources {
    // Exclude all .proto files from being copied to resources
    exclude("**/*.proto")
}
