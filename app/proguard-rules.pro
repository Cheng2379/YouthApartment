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
 -keep class androidx.core.app.CoreComponentFactory { *; }

 -obfuscationdictionary confusion_dict.txt
 -classobfuscationdictionary confusion_dict.txt
 -packageobfuscationdictionary confusion_dict.txt

 # 保留实体类
 -keep class com.cheng.youthapartment.entity.** { *; }

 # 保留Parcelize注解的类
 -keepclassmembers class * implements android.os.Parcelable {
     static ** CREATOR;
 }

 # 保留Retrofit相关
 -keepattributes Signature
 -keepattributes Exceptions
 -keep class retrofit2.** { *; }
 -keepclasseswithmembers class * {
     @retrofit2.http.* <methods>;
 }
 -keepclassmembers,allowshrinking,allowobfuscation interface * {
     @retrofit2.http.* <methods>;
 }

 # OkHttp相关
 -dontwarn okhttp3.**
 -keep class okhttp3.** { *; }
 -keep interface okhttp3.** { *; }

 # Gson相关
 -keepattributes *Annotation*
 -keep class com.google.gson.** { *; }
 -keep class * implements com.google.gson.TypeAdapterFactory
 -keep class * implements com.google.gson.JsonSerializer
 -keep class * implements com.google.gson.JsonDeserializer

 # 保留核心应用类
 -keep class com.cheng.youthapartment.App { *; }
 -keep class com.cheng.youthapartment.activity.ActivityCollector { *; }
 -keep class com.cheng.youthapartment.activity.BaseActivity { *; }

 # 保留网络请求相关类
 -keep class com.cheng.youthapartment.util.RetrofitUtil { *; }
 -keep class com.cheng.youthapartment.util.RetrofitUtil$ApiService { *; }
 -keep class com.cheng.youthapartment.util.OkHttpUtil { *; }

 # 保留工具类
 -keep class com.cheng.youthapartment.util.Logger { *; }

 # 高德地图SDK
 -keep class com.amap.api.** { *; }
 -keep class com.autonavi.** { *; }
 -keep class com.loc.** { *; }
 -dontwarn com.amap.api.**
 -dontwarn com.autonavi.**

 # 保留自定义View
 -keep class com.cheng.youthapartment.view.** { *; }

 # 注解相关
 -keepattributes *Annotation*
 -keep class * extends java.lang.annotation.Annotation { *; }

 # 保留枚举
 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 # 保留R资源类
 -keep class **.R$* { *; }

 # 保留本地方法
 -keepclasseswithmembernames class * {
     native <methods>;
 }

 # 保留序列化相关
 -keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
     private static final java.io.ObjectStreamField[] serialPersistentFields;
     private void writeObject(java.io.ObjectOutputStream);
     private void readObject(java.io.ObjectInputStream);
     java.lang.Object writeReplace();
     java.lang.Object readResolve();
 }

 # 保留WebView相关JS接口
 -keepclassmembers class * {
     @android.webkit.JavascriptInterface <methods>;
 }

 # 启用混淆后保留行号以便于调试
 -keepattributes SourceFile,LineNumberTable