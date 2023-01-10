package com.webengage.sdk.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.WindowManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.exception.AdvertisingIdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

public class WebEngageUtils {
    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");
    private static final double EPSILON = 0.0000000001;

    public static Bundle getApplicationMetaData(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getParams(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(sb.length() == 0 ? "" : "&");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }

    public static boolean CheckGZIP(HttpURLConnection con) {
        String encoding = con.getContentEncoding();
        return encoding != null
                && (encoding.equals("gzip") || encoding.equals("zip") || encoding
                .equals("application/x-gzip-compressed"));

    }

    public static String getTimezone() {
        Calendar calendar = Calendar.getInstance();
        Date currentLocalTime = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("Z", Locale.US);
        return dateFormat.format(currentLocalTime);
    }

    public static String getTimezoneId() {
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = calendar.getTimeZone();
        return timeZone.getID();
    }

    public static byte[] serialize(Object data) {
        if (data == null) {
            return null;
        }
        if (data instanceof Bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ((Bitmap) data).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } else {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
                objectOutputStream.writeObject(data);
                byte[] ar = baos.toByteArray();
                objectOutputStream.close();
                return ar;
            } catch (IOException e) {
                return null;
            }
        }

    }

    public static Object deserialize(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;

        } catch (Exception e) {
            try {
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e1) {
                return null;
            }
        }

    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int dpToPixels(float dp, Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static double getSampledValue(String entityID, String userId) {
        return Double.parseDouble(String.valueOf(Math.abs((userId + entityID).hashCode()) % 100));
    }

    public static String escapeRegex(String text) {
        if (text == null) {
            return null;
        } else {
            return REGEX_SPECIAL_CHARS.matcher(text).replaceAll("\\\\$0");
        }
    }

    public static String generateInterfaceID(Context applicationContext) {
        String m_szImei = "", m_szDevIDShort = "", m_szAdvertisementID = "", m_szWLANMAC = "", m_szBTMAC = "", m_szLongID = "";
        m_szDevIDShort = "987" +
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.USER.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10;

        try {
            //Try to fetch Advertisement ID, if not available then init with UUID
            //BEGIN
            m_szAdvertisementID = fetchGAID(applicationContext);
            if (m_szAdvertisementID.isEmpty() || m_szAdvertisementID.equalsIgnoreCase("00000000-0000-0000-0000-000000000000")) {
                m_szAdvertisementID = UUID.randomUUID().toString();
                Log.d(WebEngageConstant.TAG, "Generating UUID as advertisement is unavailable: " + m_szAdvertisementID);
            }
            //END
        } catch (Exception e) {
            m_szAdvertisementID = UUID.randomUUID().toString();
        }
        m_szLongID = m_szImei + m_szDevIDShort + m_szAdvertisementID + m_szWLANMAC + m_szBTMAC;

        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {

            return m_szLongID;
        }

        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        byte p_md5Data[] = m.digest();

        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            if (b <= 0xF) {
                m_szUniqueID += "0";
            }
            m_szUniqueID += Integer.toHexString(b);
        }

        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

    public static PackageInfo getPackageInfo(Context applicationContext) {
        try {
            return applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
        } catch (Exception e) {
        }
        return null;
    }

    public static String fetchGAID(Context applicationContext) {
        String id = "";
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext);
        if (ReflectionUtils.isAdvertisingIdDepenedencyAdded() && status == ConnectionResult.SUCCESS) {
            AdvertisingIdClient.Info info = null;
            try {
                info = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext);
            } catch (Exception e) {
                Logger.d(WebEngageConstant.TAG, "Exception while fetching advertising ID" + e.toString());
            }
            if (null != info) {
                id = info.getId();
            }
        } else {
            Logger.d(WebEngageConstant.TAG, "com.google.android.gms.ads.identifier.AdvertisingIdClient or Google play-services is missing");
        }
        return id;
    }

    public static String truncate(String str, int limit) {
        if (str != null) {
            if (str.length() > limit) {
                return str.substring(0, limit);
            } else {
                return str;
            }
        } else {
            return null;
        }
    }

    public static Map<String, Object> bundleToMap(Bundle bundle) {
        if (bundle != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            Set<String> set = bundle.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                map.put(key, bundle.get(key));
            }
            return map;
        }
        return null;
    }

    public static Bundle convertMapToBundle(Map<String, String> map) {
        Bundle bundle = null;
        if (map != null) {
            bundle = new Bundle();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        }
        return bundle;
    }

    public static Bundle mapToBundle(Map<String, Object> map) {
        Bundle bundle = null;
        try {
            if (map != null) {
                bundle = new Bundle();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    if (val instanceof String) {
                        bundle.putString(key, (String) val);
                    } else if (val instanceof String[]) {
                        bundle.putStringArray(key, (String[]) val);
                    } else if (val instanceof Boolean) {
                        bundle.putBoolean(key, (Boolean) val);
                    } else if (val instanceof boolean[]) {
                        bundle.putBooleanArray(key, (boolean[]) val);
                    } else if (val instanceof Integer) {
                        bundle.putInt(key, (int) val);
                    } else if (val instanceof int[]) {
                        bundle.putIntArray(key, (int[]) val);
                    } else if (val instanceof Long) {
                        bundle.putLong(key, (long) val);
                    } else if (val instanceof long[]) {
                        bundle.putLongArray(key, (long[]) val);
                    } else if (val instanceof Float) {
                        bundle.putFloat(key, (float) val);
                    } else if (val instanceof float[]) {
                        bundle.putFloatArray(key, (float[]) val);
                    } else if (val instanceof Double) {
                        bundle.putDouble(key, (double) val);
                    } else if (val instanceof double[]) {
                        bundle.putDoubleArray(key, (double[]) val);
                    } else if (val instanceof Bundle) {
                        bundle.putBundle(key, (Bundle) val);
                    } else if (val instanceof Character) {
                        bundle.putChar(key, (char) val);
                    } else if (val instanceof char[]) {
                        bundle.putCharArray(key, (char[]) val);
                    } else if (val instanceof CharSequence) {
                        bundle.putCharSequence(key, (CharSequence) val);
                    } else if (val instanceof CharSequence[]) {
                        bundle.putCharSequenceArray(key, (CharSequence[]) val);
                    } else if (val instanceof Byte) {
                        bundle.putByte(key, (Byte) val);
                    } else if (val instanceof byte[]) {
                        bundle.putByteArray(key, (byte[]) val);
                    } else if (val instanceof Short) {
                        bundle.putShort(key, (short) val);
                    } else if (val instanceof short[]) {
                        bundle.putShortArray(key, (short[]) val);
                    } else if (val instanceof Parcelable) {
                        bundle.putParcelable(key, (Parcelable) val);
                    } else if (val instanceof Serializable) {
                        bundle.putSerializable(key, (Serializable) val);
                    } else if (val instanceof List<?>) {
                        List list = (List) val;
                        if (list.size() > 0) {
                            Object first = list.get(0);
                            try {
                                if (first instanceof Integer) {
                                    bundle.putIntegerArrayList(key, (ArrayList<Integer>) list);
                                } else if (first instanceof String) {
                                    bundle.putStringArrayList(key, (ArrayList<String>) list);
                                } else if (first instanceof Parcelable) {
                                    bundle.putParcelableArrayList(key, (ArrayList<Parcelable>) list);
                                } else if (first instanceof CharSequence) {
                                    bundle.putCharSequenceArrayList(key, (ArrayList<CharSequence>) list);
                                }
                            } catch (Exception e) {
                                Logger.e(WebEngageConstant.TAG, "Exception while casting list to bundle", e);
                            }
                        } else {
                            Logger.e(WebEngageConstant.TAG, "ArrayList size is zero for " + key);
                        }
                    } else if (val instanceof IBinder && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        bundle.putBinder(key, (IBinder) val);
                    } else if (val instanceof SparseArray<?>) {
                        try {
                            bundle.putSparseParcelableArray(key, (SparseArray<Parcelable>) val);
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "Exception while adding SparseArray to bundle", e);
                        }
                    } else {
                        bundle.putString(key, String.valueOf(val));
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while converting map to bundle", e);
        }
        return bundle;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean areEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return true;
        }

        if (obj1 == null || obj2 == null) {
            return false;
        }

        if (obj1 instanceof Map && obj2 instanceof Map) {
            Map map1 = (Map) obj1;
            Map map2 = (Map) obj2;
            int size1 = map1.size();
            int size2 = map2.size();
            if (size1 == size2) {
                for (Object key : map1.keySet()) {
                    if (!map2.containsKey(key)) {
                        return false;
                    }

                    if (!areEqual(map1.get(key), map2.get(key))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        if (obj1 instanceof List && obj2 instanceof List) {
            List list1 = (List) obj1;
            List list2 = (List) obj2;

            int size1 = list1.size();
            int size2 = list2.size();
            if (size1 == size2) {
                for (Object i1 : list1) {
                    boolean contains = false;
                    for (Object i2 : list2) {
                        if (areEqual(i1, i2)) {
                            contains = true;
                            break;
                        }
                    }

                    if (!contains) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        if (obj1 instanceof Number && obj2 instanceof Number) {
            Number n1 = (Number) obj1;
            Number n2 = (Number) obj2;
            return Math.abs(n1.doubleValue() - n2.doubleValue()) < EPSILON;
        }

        if (obj1 instanceof CharSequence && obj2 instanceof CharSequence) {
            String strObj1 = String.valueOf(obj1);
            String strObj2 = String.valueOf(obj2);
            return strObj1.equals(strObj2);
        }

        return obj1.equals(obj2);
    }

    public static String readEntireStream(InputStream inputStream) {
        return NetworkUtils.readEntireStream(inputStream);
    }
}
