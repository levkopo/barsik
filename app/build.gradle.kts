import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "2.0.21"
}

group = "ru.levkopo.barsik"
version = "0.1"

dependencies {

}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "zation.server.api.MainKt"
        )
    }

    from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
        .exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}