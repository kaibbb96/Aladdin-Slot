
#region [ Default Required ]
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
#endregion

#region [ JavaScript Interface Classes ]
-keepclassmembers class dev.jo.tcplatform.base.AndroidInterfaceH5 {
   public *;
}
#endregion

#region [ AppsFlyer ProGuard ]
-keep class com.appsflyer.** { *; }
#endregion

#region [ Google Services ]
#Notes: Required for Adjust / AppsFlyer
-keep public class com.android.installreferrer.** { *; }
#endregion

-dontwarn com.alipay.sdk.app.H5PayCallback
-dontwarn com.alipay.sdk.app.PayTask
-dontwarn com.download.library.DownloadImpl
-dontwarn com.download.library.DownloadListenerAdapter
-dontwarn com.download.library.DownloadTask
-dontwarn com.download.library.ResourceRequest