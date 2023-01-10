package com.webengage.sdk.android.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * The parameter used in Class.forName("..") api is also obfuscated,if minify enabled is set true,so things works as they should
 * be.But parameter used in getMethod api of class is not obfuscated.Hence,never use getMethod api to check for method.
 */
public class ReflectionUtils {
    public interface ScanResult {
        void onScanCompleted(Map<Class<?>, List<Object>> callbackImplementationMap);
    }


    public static boolean isGoogleCloudMessagingDependencyAdded() {
        try {
            Class.forName("com.google.android.gms.gcm.GoogleCloudMessaging");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isFusedLocationDependencyAdded() {
        try {
            Class.forName("com.google.android.gms.location.LocationServices");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isGoogleApiClientDependencyAdded() {
        try {
            Class.forName("com.google.android.gms.common.api.GoogleApiClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isLocationResultPresent() {
        try {
            Class.forName("com.google.android.gms.location.LocationResult");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isGeoFencePresent() {
        try {
            Class.forName("com.google.android.gms.location.Geofence");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isGeoFenceRequestPresent() {
        try {
            Class.forName("com.google.android.gms.location.GeofencingRequest");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static boolean isGeoFenceEventPresent() {
        try {
            Class.forName("com.google.android.gms.location.GeofencingEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isAdvertisingIdDepenedencyAdded() {
        try {
            Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static boolean isCorodvaPresent() {
        try {
            Class.forName("com.webengage.cordova.WebEngagePlugin");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isInstallReferrerPresent() {
        try {
            Class.forName("com.android.installreferrer.api.InstallReferrerStateListener");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object[] args) {
        try {
            Class<?> c = Class.forName(className);
            Method method = c.getMethod(methodName, parameterTypes);
            return method.invoke(null, args);
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return null;
    }

    public static Object invokeConstructor(String className, Class<?>[] classTypes, Object[] args) {
        try {
            Class fooClass = Class.forName(className);
            Constructor constructor = fooClass.getDeclaredConstructor(classTypes);
            constructor.setAccessible(true);
            Object object = constructor.newInstance(args);
            constructor.setAccessible(false);
            return object;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }

    }

    public static Object invokeConstructor(String className) {
        try {
            Class fooClass = Class.forName(className);
            Constructor constructor = fooClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object object = constructor.newInstance();
            constructor.setAccessible(false);
            return object;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

}
