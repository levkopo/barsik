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
        mainClass = "ru.levkopo.barsik.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "Barsik"
            packageVersion = "1.0.0"

            windows {
                shortcut = true
                menuGroup = packageName
                iconFile.set(project.file("app_icon.ico"))
            }

            linux {
                iconFile.set(project.file("app_icon.png"))
            }
        }

        buildTypes.release.proguard {
            isEnabled.set(false)
        }
    }
}

dependencies {
    // Add common library
    implementation(project(":common"))

    // Add compose
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.components.resources)

    // Excelkt
    implementation("io.github.evanrupert:excelkt:1.0.2")

    // JTransforms
    implementation("com.github.wendykierp:JTransforms:3.1")

    // Charts
    implementation("org.jfree:jfreechart:1.5.5")
    implementation("com.patrykandpatrick.vico:multiplatform:2.1.0-alpha.6")
    implementation("com.patrykandpatrick.vico:multiplatform-m3:2.1.0-alpha.6")

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
            "Main-Class" to "ru.levkopo.barsik.MainKt"
        )
    }

//    include(sourceSets.getByName("main").resources.includes)

    from(configurations.runtimeClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
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