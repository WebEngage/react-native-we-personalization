# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/shahrukhimam/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-keepattributes InnerClasses
-keepattributes Signature
-keepattributes Deprecated
-keepattributes EnclosingMethod
-keepparameternames
-dontwarn com.webengage.sdk.android.**
-keep public class * extends android.app.Service
-keep public class * extends android.app.Activity
-keep public class * extends android.content.BroadcastReceiver
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.webengage.sdk.android.AbstractWebEngage{
   public <methods>;
 }
-keep class com.webengage.sdk.android.WebEngage {
   public <methods>;
}
-keep class com.webengage.sdk.android.WebEngageConfig{
    public <methods>;
}
-keep class com.webengage.sdk.android.WebEngageConfig$*{
    public <methods>;
}
-keep class com.webengage.sdk.android.PushChannelConfiguration{
    public <methods>;
}

-keep class com.webengage.sdk.android.PushChannelConfiguration$*{
    public <methods>;
}
-keep class com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks{*;}
-keep class com.webengage.sdk.android.Logger{ *;}

-keep class com.webengage.sdk.android.Analytics{
   public <methods>;
}
-keep class com.webengage.sdk.android.Analytics$*{
   public <methods>;
}

-keep class com.webengage.sdk.android.User{*;}
-keep class com.webengage.sdk.android.callbacks.LifeCycleCallbacks{ *;}
-keep class com.webengage.sdk.android.callbacks.PushNotificationCallbacks{*;}
-keep class com.webengage.sdk.android.callbacks.InAppNotificationCallbacks{*;}
-keep class com.webengage.sdk.android.callbacks.StateChangeCallbacks{*;}
-keep class com.webengage.sdk.android.callbacks.CustomPushRender{*;}
-keep class com.webengage.sdk.android.callbacks.CustomPushRerender{*;}
-keep class com.webengage.sdk.android.UserProfile$Builder{*;}
-keep class com.webengage.sdk.android.actions.database.ReportingStrategy{*;}
-keep class com.webengage.sdk.android.LocationTrackingStrategy{*;}
-keep public class com.webengage.sdk.android.actions.render.PushNotificationData$*{
    *;
}
-keep public class com.webengage.sdk.android.actions.render.PushNotificationData{
    *;
}
-keep public class com.webengage.sdk.android.actions.render.InAppNotificationData$*{
    *;
}
-keep public class com.webengage.sdk.android.actions.render.InAppNotificationData{
    *;
}
-keep public class com.webengage.sdk.android.actions.render.CallToAction{
    *;
}

-keep public class * extends com.webengage.sdk.android.actions.render.CallToAction{
    *;
}
-keep enum com.webengage.sdk.android.actions.render.CallToAction$TYPE{*;}
-keepclassmembers class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}
-keep class com.webengage.sdk.android.Channel{*;}
-keep class com.webengage.sdk.android.actions.rules.RuleExecutor{*;}
-keep class com.webengage.sdk.android.utils.Gender{*;}
-keep class com.webengage.sdk.android.utils.WebEngageCallback{*;}
-keep enum com.webengage.sdk.android.utils.WebEngageConstant$STYLE{*;}
-keep class com.webengage.sdk.android.actions.exception.GCMRegistrationException{*;}
-keep class com.webengage.sdk.android.actions.exception.WebViewException{*;}
-keep class com.webengage.sdk.android.actions.exception.AdvertisingIdException{*;}
-keep class com.webengage.sdk.android.actions.exception.ImageLoadException{*;}
-keep enum com.webengage.sdk.android.utils.DataType{
    **[] $VALUES;
    public *;
}

-keep enum com.webengage.sdk.android.utils.Provider{
    **[] $VALUES;
    public *;
}
-keep public class com.webengage.sdk.android.bridge.WebEngageMobileBridge{
    *;
}

-keep public class com.webengage.sdk.android.actions.database.DataHolder
-keepclassmembers public class com.webengage.sdk.android.actions.database.DataHolder {
    public static com.webengage.sdk.android.actions.database.DataHolder get();
    public java.util.Map container;
}

-keep public class com.webengage.sdk.android.PendingIntentFactory
-keep public class com.webengage.sdk.android.PendingIntentFactory {
    public *;
}
-keep public class com.webengage.sdk.android.PushUtils
-keep public class com.webengage.sdk.android.PushUtils {
    public *;
}

-keep public class com.webengage.sdk.android.utils.htmlspanner.WEHtmlParserInterface {
    public *;
}
-keep class com.webengage.sdk.android.InLinePersonalizationListener{*;}
-keepclassmembers public class com.webengage.sdk.android.utils.WebEngageUtils {
 public static *** readEntireStream(...);
}
-keepclasseswithmembernames public class com.webengage.sdk.android.utils.WebEngageUtils {
                            public static *** readEntireStream(...);
                           }

