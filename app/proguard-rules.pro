
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface

-keep class com.appsflyer.** { *; }

-keep public class com.android.installreferrer.** { *; }


-dontwarn com.alipay.sdk.app.H5PayCallback**
-dontwarn com.alipay.sdk.app.PayTask**
-dontwarn com.download.library.DownloadImpl**
-dontwarn com.download.library.DownloadListenerAdapter**
-dontwarn com.download.library.DownloadTask**
-dontwarn com.download.library.ResourceRequest**