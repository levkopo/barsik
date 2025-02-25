import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.compose.desktop.application.dsl.TargetFormat


plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.compose")
}

group = "ru.levkopo.barsik"
version = "0.1"

configurations {
    create("corba")
}

sourceSets {
    this.getByName("main").java.srcDirs("build/generated/sources/jacorbIDL")
}

compose.desktop {
    application {
        mainClass = "zation.server.api.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinJvmComposeDesktopApplication"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    // Add common library
    implementation(project(":common"))

    // Add compose
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    implementation("commons-net:commons-net:3.11.1")
    implementation("org.jboss.spec.javax.rmi:jboss-rmi-api_1.0_spec:1.0.6.Final")
    implementation("org.jacorb:jacorb:3.2")
    implementation("io.github.vincenzopalazzo:material-ui-swing:1.1.4")

    val corba by configurations
    corba("org.jacorb:jacorb-idl-compiler:3.9")
    corba("org.jacorb:jacorb-omgapi:3.9")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "zation.server.api.MainKt"
        )
    }

//    include(sourceSets.getByName("main").resources.includes)

    from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
        .exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

repositories {
    mavenCentral()
    google()
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