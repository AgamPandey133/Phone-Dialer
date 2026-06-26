# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Moshi adapters
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Keep Room entities
-keep class com.smartdialer.app.data.local.entity.** { *; }

# Keep domain models
-keep class com.smartdialer.app.domain.model.** { *; }
