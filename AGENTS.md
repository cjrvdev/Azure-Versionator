# AGENTS.md

## Project Snapshot
- Kotlin Multiplatform + Compose project with 3 modules: `:shared` (UI/domain/data), `:androidApp` (Android host), `:desktopApp` (JVM/Desktop host).
- **Azure Versionator**: an Azure DevOps version management app with cross-platform support.
- Entry points are thin wrappers that call shared `App()`:
  - Android: `androidApp/src/main/kotlin/.../MainActivity.kt` (calls `App()` via `setContent()`).
  - Desktop: `desktopApp/src/main/kotlin/.../main.kt` (calls `App()` inside `application { Window(...) }`).
- Main architecture is in `shared/src/commonMain/kotlin/dev/cjrv/azureversionator/`:
  - `App.kt` — Compose root with Koin DI setup and theme.
  - `navigation/` — Route definitions and Navigation3 integration.
  - `ui/features/` — Screens and ViewModels.
  - `di/` — Koin modules (Data, ViewModels, Navigation, Platform).
  - `data/` — API clients, repositories, and settings.

## Architecture and Data Flow
- **DI & State Management:**
  - `App()` in `shared/src/commonMain/kotlin/dev/cjrv/azureversionator/App.kt` wraps the UI in `KoinApplication` and applies the theme.
  - Koin provides DI across all targets; modules are loaded in `di/KoinInitializer.kt`.
  - UI state is owned by ViewModels (`HomeViewModel`, `SettingsViewModel`, `NewVersionViewModel`) in `ui/features/`.
- **Navigation:**
  - Routes are sealed interfaces marked with `@Serializable` in `navigation/Route.kt` (e.g., `data object Home`, `data object Settings`).
  - `Navigation.kt` uses `NavDisplay` with `koinEntryProvider<Any>()` to auto-discover navigation entries from Koin modules.
  - Add new screens by: (1) define route in `Route.kt`, (2) create Screen composable in `ui/features/`, (3) add `navigation<Route> { }` entry in `navigationModule`.
- **Data Layer:**
  - `di/DataModule.kt` provides: `AzureDevOpsApi` (for API calls), `HttpClient` (with platform-specific engines), and `Settings` (via `multiplatform-settings`).
  - `data/network/AzureDevOpsApi.kt` (interface) + `AzureDevOpsApiImpl.kt` follow repository pattern.
  - `data/network/HttpClientProvider.kt` (expect/actual) returns platform-specific Ktor engines (OkHttp for Android, CIO for JVM).
- **Platform-specific code:**
  - Expect/actual for HTTP engines: `shared/src/commonMain/.../HttpClientProvider.kt` + `.android.kt` / `.jvm.kt`.
  - Platform modules in `di/PlatformModule.kt` (expect) + `.android.kt` / `.jvm.kt` (actual) for OS-specific DI bindings.

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
- Build plugins in use: `kotlinMultiplatform`, `androidMultiplatformLibrary`, `composeMultiplatform`, `composeCompiler`, `kotlinxSerialization` (applied in `shared/build.gradle.kts`).
- Routes are `@Serializable` for Navigation3 type safety; sealed interfaces allow multi-interface implementation (e.g., `MyRoute : Route & TabItem`).
- Compose resources are generated and referenced as `Res.drawable.*` (see `App.kt`). Add via IDE resource manager or manual placement in `shared/src/commonMain/composeResources/`.

## Key Dependencies and Integration Points
- **Shared dependencies** (in `shared/build.gradle.kts`):
  - Compose runtime/foundation/material3/ui (multiplatform).
  - **Lifecycle & Navigation:** `androidx.lifecycle:lifecycle-viewmodel-compose` + Navigation3 (`org.jetbrains.androidx.navigation3:navigation3-ui`) for routing.
  - **DI:** Koin 4.2+ with `koin-compose` and `koin-compose-navigation3` for declarative navigation entries.
  - **Networking:** Ktor 3.5+ client core with JSON serialization (`kotlinx.serialization`).
  - **Settings:** `com.russhwolf:multiplatform-settings-no-arg` (cross-platform `SharedPreferences`/`Preferences`).
- **Platform-specific Ktor engines:**
  - Android: OkHttp (added in `shared/build.gradle.kts` androidMain deps).
  - JVM/Desktop: CIO (added in `shared/build.gradle.kts` jvmMain deps).
- **Additional Android host** (`androidApp/build.gradle.kts`):
  - `androidx.activity:activity-compose` + `androidx.core:core-splashscreen` for app bootstrap.
- **Build plugins:** All via `gradle/libs.versions.toml`; versions are centralized (Kotlin 2.4.0, Compose Multiplatform 1.11.1, Koin 4.2.2).

## Practical Agent Guidance
- **Adding new screens/routes:**
  - Define route in `shared/src/commonMain/.../navigation/Route.kt` as `@Serializable data object/class MyRoute : Route`.
  - Create `MyScreen.kt` in `shared/src/commonMain/.../ui/features/` with a `@Composable fun MyScreen(...)` that calls a `ViewModel`.
  - Create `MyViewModel.kt` in the same folder; inject dependencies via Koin.
  - Add entry in `di/NavigationModule.kt`: `navigation<MyRoute> { MyScreen(...) }`.
  - That's it — `Navigation.kt` auto-discovers it via `koinEntryProvider`.
- **Adding cross-platform features:**
  - Implement UI/domain logic in `shared/commonMain`.
  - Use `expect/actual` for platform APIs (e.g., file access, native sensors). Place expect in `shared/commonMain`, actual in `.android.kt` / `.jvm.kt`.
  - Register platform implementations in `di/PlatformModule.kt` (expect) and its `.android.kt` / `.jvm.kt` pairs.
- **Adding API endpoints:**
  - Add method to `data/network/AzureDevOpsApi.kt` interface.
  - Implement in `AzureDevOpsApiImpl.kt`.
  - `HttpClient` is already injected; inject `AzureDevOpsApi` in your ViewModel.
- **Data storage (user settings):**
  - Use `Settings` from Koin (initialized in `dataModule`); it's already multiplatform.
  - `data/settings/AzureSettingsRepository.kt` wraps Settings; inject it in ViewModels as needed.
- **Validating changes:**
  - Run at least one Android build: `./gradlew :shared:compileDebugKotlinAndroid :androidApp:assembleDebug`.
  - Run desktop compile: `./gradlew :desktopApp:compileKotlin`.
  - If adding dependencies, update `gradle/libs.versions.toml` first, then use `libs.*` aliases in build scripts.
- **General principles:**
  - Keep feature code in `:shared/src/commonMain`; `:androidApp` and `:desktopApp` are minimal bootstrap entry points.
  - Package namespace: always `dev.cjrv.azureversionator.*`.
  - Use sealed interfaces (not sealed classes) for extensible route hierarchies.

