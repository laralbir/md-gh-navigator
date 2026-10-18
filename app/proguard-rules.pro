# ProGuard / R8 rules for MD GitHub Navigator

# Keep Models and Serialization
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Google Tink
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
  <fields>;
}

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions

# Strip debug and authorization log strings in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

