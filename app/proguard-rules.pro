# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/pengjianbo/Documents/dev/android_dev/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#########################通用混淆配置，项目中有就不需要配置#####################
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}

#########################PJSDK混淆配置#####################
-keep class com.paojiao.sdk.*{*;}
-keep class com.paojiao.sdk.service.*{*;}
-keepclassmembers class com.paojiao.sdk.H5WebViewActivity$PJJavascriptInterface {
  public *;
}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*