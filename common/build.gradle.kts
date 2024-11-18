import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "2.0.21"
}

group = "ru.levkopo.barsik"
version = "0.1"

configurations {
    create("corba")
}

sourceSets {
    this.getByName("main").java.srcDirs("build/generated/sources/jacorbIDL")
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    api("org.jboss.spec.javax.rmi:jboss-rmi-api_1.0_spec:1.0.6.Final")
    api("org.jacorb:jacorb:3.2")

    val corba by configurations
    corba("org.jacorb:jacorb-idl-compiler:3.9")
    corba("org.jacorb:jacorb-omgapi:3.9")
}

tasks.create<JavaExec>("buildCorba") {
    dependsOn(tasks.clean)

    group = "build"
    mainClass.set("org.jacorb.idl.parser")
    classpath = configurations.getByName("corba")
    args = listOf(
        "-d", "build/generated/sources/jacorbIDL",
        "barsik.idl",
    )
}

tasks.build {
    dependsOn(tasks.getByName("buildCorba"))
}

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/")
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