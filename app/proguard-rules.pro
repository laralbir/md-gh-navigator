# ProGuard / R8 rules for MD GitHub Navigator

# 1. Attributes & Annotations
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions

# 2. WebView JavaScript Interfaces (ReaderBridge)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.laralbir.mdghnavigator.core.renderer.ReaderBridge { *; }

# 3. AndroidX Room Database & DAOs
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <methods>;
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * {
    public <methods>;
}
-dontwarn androidx.room.paging.**

# 4. Google Tink & Android Keystore
-keep class com.google.crypto.tink.** { *; }
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
    <fields>;
}

# 5. Retrofit 2, OkHttp 3 & Okio
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn javax.annotation.**
-keepattributes Signature
-keepattributes Exceptions

# 6. AndroidSVG & Coil Image Decoding
-keep class com.caverock.androidsvg.** { *; }
-dontwarn com.caverock.androidsvg.**

# 7. Kotlin Coroutines & Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
    @kotlinx.serialization.Serializable <fields>;
}

# 8. Strip debug logging from release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
