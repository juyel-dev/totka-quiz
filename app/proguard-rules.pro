# Keep Gson models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.juyel.totka.data.model.** { *; }
-keep class com.google.gson.** { *; }
