import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

group = "dev.jvqtil.cuber"
version = "1.3.0"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")

    implementation("androidx.room:room-runtime-jvm:2.8.4")
    implementation("androidx.sqlite:sqlite-bundled-jvm:2.7.0")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation("org.worldcubeassociation.tnoodle:lib-scrambles:0.19.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("net.java.dev.jna:jna:5.19.1")
    implementation("net.java.dev.jna:jna-platform:5.19.1")
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "dev.jvqtil.cuber.desktop.MainKt"
            jvmArgs(
            "--enable-native-access=ALL-UNNAMED",
        )

        nativeDistributions {
            packageName = "Cuber Desktop"
            packageVersion = version.toString()
            description = "A focused speedcubing timer and solve tracker"
            vendor = "Cuber"
            copyright = "© 2026 Cuber"

            modules(
                "java.sql",
                "java.desktop"
            )

            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Msi,
                TargetFormat.Dmg,
                TargetFormat.Pkg,
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.AppImage
            )

            macOS {
                bundleID = "dev.jvqtil.cuber.desktop"
                packageName = "Cuber Desktop"
                iconFile.set(project.file("resources/cuber.icns"))
                jvmArgs(
                    "-Dapple.awt.application.appearance=system",
                )
            }

            windows {
                menuGroup = "Cuber"
                upgradeUuid = "79d1c5bb-6f1a-4c08-9842-f0771ec7a0d2"
                iconFile.set(project.file("resources/cuber.ico"))
            }

            linux {
                iconFile.set(project.file("resources/cuber.png"))
            }
        }
    }
}
