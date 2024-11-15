import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "2.0.21"
}

group = "ru.levkopo.barsik.emu"
version = "0.1"

dependencies {
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
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}