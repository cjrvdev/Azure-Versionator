# AGENTS.md

## Project Snapshot
- Azure Versionator is an Azure DevOps utility for triggering pipeline builds and downloading work item attachments.
- Kotlin Multiplatform + Compose with three modules: `:shared` (UI, state and data), `:androidApp` (Android host), and `:desktopApp` (JVM host).
- Keep feature logic in `shared\src\commonMain\kotlin\dev\cjrv\azureversionator`; hosts should stay focused on platform bootstrapping.
- Android `MainActivity.kt` and desktop `main.kt` initialize FileKit and call shared `App()`.
- Package namespace: `dev.cjrv.azureversionator.*`.
- Dependency and plugin versions belong in `gradle\libs.versions.toml`; use its aliases rather than hardcoded versions. Consult the catalog and Gradle wrapper instead of relying on version numbers in documentation.

## Architecture and Data Flow
- `App.kt` starts `KoinApplication`, observes `AppSettingsRepository`, and applies `AzureVersionatorTheme`.
- `di\KoinInitializer.kt` loads `dataModule`, `viewModelModule`, `navigationModule`, and `platformModule()`.
- Screens and their ViewModels live together in `ui\features`. Current features are Home, Edit Profile, New Version, and Attachment Bulk Downloader.
- ViewModels expose `StateFlow` UI state and use `viewModelScope` for asynchronous work; register them with `viewModelOf` in `di\ViewModelModule.kt`.
- `data\network\AzureDevOpsApi.kt` defines Azure API operations; `AzureDevOpsApiImpl.kt` implements them using the injected Ktor `HttpClient`.
- `di\DataModule.kt` configures JSON content negotiation, Settings, repositories, and `ProfileTransferService`.
- `AzureSettingsRepository` and `AzureSettingsRepositoryImpl` manage profiles and the active profile. `AppSettingsRepository` and its implementation manage application settings.
- Use these repositories rather than accessing persistence directly from UI code.

## Navigation
- Routes in `navigation\Route.kt` are `@Serializable` data objects/classes implementing the sealed `Route` interface, which extends `NavKey`.
- Existing routes are `Home`, `EditProfile`, `NewVersion`, and `AttachmentBulkDownloader`.
- `Navigator` owns the back stack and provides `navigateTo` / `goBack`.
- `Navigation.kt` uses `NavDisplay`, `koinEntryProvider<Any>()`, and saveable-state/ViewModel entry decorators.
- To add a screen: define its route, implement its screen and ViewModel, register the ViewModel, and add `navigation<MyRoute> { ... }` in `di\NavigationModule.kt`.
- Pass navigation callbacks to screens as existing entries do. New entries are discovered through Koin; the central host normally needs no change.
- Prefer sealed interfaces for route hierarchies; Kotlin multiple-interface syntax is `MyRoute : Route, AnotherInterface`.

## Shared UI and Theme
- Reuse `ui\composables`: `TechPanel`, `TechCard`, `TechLabel`, `TechIcon`, `TechToggleChip`, custom buttons, text fields, and app bars.
- Screen-specific dialogs can remain private composables in their screen file; extract reusable controls into `ui\composables`.
- `theme\Color.kt` defines light/dark palettes; `Theme.kt` maps them to `MaterialExpressiveTheme` and provides `LocalDarkTheme`.
- Use `MaterialTheme.colorScheme`, shared typography and `theme\Dimensions.kt`; preserve consistent panel styling across screens and check both themes.
- Hover effects should also support keyboard focus via a shared `MutableInteractionSource`, as in Home's operation cards and attribution link.
- `TechLabel` defaults to the primary color. In disabled text-field labels, inherit `LocalContentColor.current` from the enclosing Material field instead of forcing the primary color.
- `CustomSearchableDropdownField` filters option labels without case sensitivity. It only commits existing options; dismissing the menu discards the search and restores the selected label.
- Variable editors use `TextFieldType.SingleLine` or `TextFieldType.Multiline` to choose the appropriate shared text field. Preserve required/secret flags.
- User-facing strings belong in `shared\src\commonMain\composeResources\values\strings.xml`; access generated resources through `Res.string.*` / `Res.drawable.*`.

## Profile Import and Export
- `data\profiles\ProfileTransferService.kt` uses FileKit JSON open/save dialogs; it is injected into Home and Edit Profile ViewModels.
- Home imports a single profile; Edit Profile exports the saved active profile, not unsaved form edits.
- `data\model\app\SharedProfile.kt` is the transfer DTO. It includes name, organization, team project, variables and filters, but deliberately excludes the profile ID and Personal Access Token (PAT).
- Import creates a new profile ID with an empty PAT, saves the profile, and makes it active. It does not merge into an existing profile.
- The JSON currently has no format-version or migration layer.
- Variable values are exported even when `isSecret` is true. Do not describe these files as free of secrets; the README warns users to review them before sharing.
- Keep export serialization based on `SharedProfile`, not the persisted `Profile`, to avoid exporting the PAT.
- Transfer results distinguish cancellation, success and failure. Preserve ViewModel/UI feedback and treat picker cancellation as a no-op.

## Platform Integration
- Shared HTTP engine declarations live in `data\network\HttpClientProvider.kt`, with Android/JVM actuals in their source sets: OkHttp on Android, CIO on JVM.
- `di\PlatformModule.kt` has platform-specific actuals for bindings such as `OpenUrlService` and `AttachmentFileService`.
- Prefer existing FileKit APIs for file dialogs; keep platform-specific file handling in the Android/JVM implementations.
- Preserve FileKit initialization in both hosts when changing bootstrap code.
- `:shared` uses `androidLibrary { ... }` inside KMP with the Android Multiplatform Library plugin, not a conventional Android library plugin.
- Android host and shared Android compilation target JVM 11. Do not confuse this bytecode target with the JDK needed to run Gradle.

## Build and Validation
- Use the checked-in Gradle wrapper and module-scoped tasks from the repository root.
- Windows PowerShell commands:
  ```powershell
  .\gradlew.bat :desktopApp:run
  .\gradlew.bat :shared:compileKotlinJvm :shared:compileAndroidMain
  .\gradlew.bat :androidApp:assembleDebug :desktopApp:compileKotlin
  .\gradlew.bat :desktopApp:createReleaseDistributable
  ```
- In POSIX shells, use `./gradlew` with the same task names.
- The shared Android compile task is `:shared:compileAndroidMain`, not `:shared:compileDebugKotlinAndroid`.
- For shared code/UI changes, validate Android assembly and desktop compilation. Documentation-only changes do not require a build.
- No Kotlin test files are currently present; compilation does not prove runtime behavior. For UI changes, manually exercise affected flows, disabled states, keyboard focus, and both themes where applicable.
- Profile transfer changes need export/import round-trip checks, cancellation/failure feedback checks, and confirmation that the PAT is absent and the imported profile has a new ID.

## Desktop Releases and ProGuard
- `desktopApp\build.gradle.kts` configures native distributions and loads `desktopApp\proguard-rules.pro`.
- Release ProGuard optimization is explicitly disabled to avoid the Navigation3 verifier failure previously encountered. Shrinking remains enabled; obfuscation is off under the current Compose defaults.
- Existing keep rules preserve Ktor's `KotlinxSerializationJsonExtensionProvider` (loaded through `ServiceLoader`) and FileKit's Windows backend plus JNA (reflection/COM).
- Do not remove these rules merely because application code has no reflective calls: dependency internals also need them.
- After dependency or ProGuard changes, build and launch the release distribution, navigate between screens, exercise a JSON API call, and open import/export file dialogs on the target OS. Debug builds or successful packaging alone do not validate shrinking.
- Close the running distribution before rebuilding it on Windows to avoid locked JARs.
