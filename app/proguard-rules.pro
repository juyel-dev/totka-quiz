# Gson নির্দিষ্ট রুলস
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.juyel.totka.data.models.** { *; }
-keep class com.juyel.totka.data.model.** { *; }
-keep class com.google.gson.** { *; }

# Room Database রুলস
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.Entity
-keep class androidx.room.Entity { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
