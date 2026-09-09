# Proguard Rules for SIGHTGUIDE Application

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Room
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}

# ML Kit
-keep class com.google.mlkit.** { *; }

# Keep Compose
-keepclassmembers class * extends androidx.compose.ui.Modifier { *; }
