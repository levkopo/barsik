import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

group = "ru.levkopo.barsik.emu"
version = "0.1"

dependencies {
    // Add compose
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.components.resources)

    // JTransforms
    implementation("com.github.wendykierp:JTransforms:3.1")

    implementation(project(":common"))
    implementation("com.khubla.ktelnet:ktelnet:1.1")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "ru.levkopo.barsik.emu.MainKt"
        )
    }

    from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
        .exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://oss.sonatype.org/content/repositories/")
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}