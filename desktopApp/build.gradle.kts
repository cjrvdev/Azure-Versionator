import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "dev.cjrv.azureversionator.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.cjrv.azureversionator"
            packageVersion = "1.0.1"
            macOS{
                iconFile.set(rootProject.file("shared/src/commonMain/composeResources/drawable/appicon_macos.icns"))
            }
            windows {
                iconFile.set(rootProject.file("shared/src/commonMain/composeResources/drawable/appicon_windows.ico"))
            }
            linux {
                iconFile.set(rootProject.file("shared/src/commonMain/composeResources/drawable/appicon.png"))
            }
        }
    }
}