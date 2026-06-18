# AGENTS.md

## Project Snapshot
- Kotlin Multiplatform + Compose project with 3 modules: `:shared` (UI/domain), `:androidApp` (Android host), `:desktopApp` (JVM host).
- Entry points are thin wrappers that call shared `App()`:
  - Android: `androidApp/src/main/kotlin/dev/cjrv/azureversionator/MainActivity.kt`
  - Desktop: `desktopApp/src/main/kotlin/dev/cjrv/azureversionator/main.kt`
- Main UI and common logic currently live in `shared/src/commonMain/kotlin/dev/cjrv/azureversionator/`.

## Architecture and Data Flow
- `App()` in `shared/src/commonMain/kotlin/dev/cjrv/azureversionator/App.kt` is the shared Compose root and owns UI state (`remember { mutableStateOf(...) }`).
- Platform-specific behavior uses `expect/actual`:
  - Expect API: `shared/src/commonMain/.../Platform.kt`
  - Android actual: `shared/src/androidMain/.../Platform.android.kt`
  - JVM actual: `shared/src/jvmMain/.../Platform.jvm.kt`
- Current sample flow is UI -> `Greeting().greet()` -> `sayHello(...)` + `getPlatform()`.

## Build and Run Workflows
- Use Gradle wrapper and module-scoped tasks.
- Common commands (from `README.md`):
  - `./gradlew :androidApp:assembleDebug`
  - `./gradlew :desktopApp:run`
  - `./gradlew :desktopApp:hotRun --auto`
- Full compile check across modules:
  - `./gradlew :shared:compileKotlinJvm :shared:compileDebugKotlinAndroid :androidApp:assembleDebug :desktopApp:compileKotlin`
- There are currently no Kotlin test files (`*Test*.kt` not present), so avoid assuming test coverage exists.

## Project-Specific Conventions
- Keep most feature code in `:shared`; keep `:androidApp` and `:desktopApp` focused on platform bootstrapping.
- Use version catalog aliases from `gradle/libs.versions.toml` in build scripts (avoid hardcoded dependency versions).
- `:shared` uses `androidLibrary { ... }` inside KMP config (AGP 9 Kotlin Multiplatform library plugin), not a separate Android-only module.
- Java/Kotlin target is JVM 11 in Android and shared config.
- Generated Compose resources are referenced as `Res.drawable.*` (see `App.kt`).

## Key Dependencies and Integration Points
- Declared in `shared/build.gradle.kts`: Compose runtime/foundation/material3/ui, lifecycle compose, Navigation3, Koin, Ktor, multiplatform-settings.
- Android host adds OkHttp engine for Ktor in `androidApp/build.gradle.kts` (`libs.ktor.client.okhttp`).
- Dependency/plugin versions are centralized in `gradle/libs.versions.toml`; update there first, then consume via `libs.*` aliases.

## Practical Agent Guidance
- When adding cross-platform features: implement UI/domain in `shared/commonMain`; add `expect/actual` only for platform APIs.
- When adding Android-only behavior: wire it in `androidApp` (and `shared/androidMain` if common code needs platform implementation).
- Keep package namespace consistent: `dev.cjrv.azureversionator`.
- Validate changes with at least one Android build task and one desktop/shared compile task before handing off.

