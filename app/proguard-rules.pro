# To show line numbers in stack traces reported to Firebase
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# OkHttp
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Model classes for serialization
-keep class io.levelsoftware.carculator.model.** { *; }

# Android support library
-keep class android.support.v7.widget.SearchView { *; }

# Glide
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

