# Ktor discovers this JSON converter through Java's ServiceLoader.
-keep class io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider { *; }

# FileKit's Windows dialogs use JNA/COM interfaces discovered through reflection.
-keep class io.github.vinceglb.filekit.dialogs.platform.windows.** { *; }
-keep class com.sun.jna.** { *; }
