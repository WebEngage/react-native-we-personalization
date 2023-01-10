# COPIED FROM /Users/shahrukhimam/Library/Android/sdk/tools/proguard/proguard-android.txt JUST TO COMMENT -dontpreverify flag below

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize


#-dontpreverify -> THIS FLAG WAS CAUSING TEST FAILURE FOR SEGMENT INTEGRATION


# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepattributes InnerClasses
-keepattributes Signature
-keepattributes Deprecated
-keepattributes EnclosingMethod
-keepparameternames
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn com.webengage.personalization.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
#For enums
-keepclassmembers class **$WhenMappings {
    <fields>;
}
#
#-keep class com.webengage.personalization.WEPersonalization {*;}
-keepclassmembers class com.webengage.personalization.WEPersonalization {
    public static ** Companion;
}
-keepattributes RuntimeVisibleAnnotations

-keep class com.webengage.personalization.AbstractInLinePersonalization {*;}
-keepclassmembers class com.webengage.personalization.AbstractInLinePersonalization {
    public <methods>;
}
-keep class com.webengage.personalization.callbacks.WEPlaceholderCallback {*;}
-keep class com.webengage.personalization.callbacks.WECampaignCallback {*;}
-keep class com.webengage.personalization.data.WECampaignData {*;}
-keep class com.webengage.personalization.data.WECampaignContent {*;}
-keep class com.webengage.personalization.utils.Properties {*;}
