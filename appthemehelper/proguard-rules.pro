# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class zzh.lifeplayer.appthemehelper.** { *; }
# 保留 Material 组件相关资源
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

-keep class com.google.android.** { *; }
-dontwarn com.google.android.**

-keep class com.google.android.datatransport.** { *; }
-dontwarn com.google.android.datatransport.**

-keep class com.afollestad.materialdialogs.input.InputUtilExtKt { *; }
-dontwarn com.afollestad.materialdialogs.input.**