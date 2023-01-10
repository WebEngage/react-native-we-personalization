package com.webengage.sdk.android.bridge;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.webengage.sdk.android.Channel;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.UserProfile;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.Gender;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shahrukhimam on 22/07/17.
 */

public class WebEngageMobileBridge {

    private static final String FIRST_NAME = "we_first_name";
    private static final String LAST_NAME = "we_last_name";
    private static final String EMAIL = "we_email";
    private static final String BIRTH_DATE = "we_birth_date";
    private static final String PHONE = "we_phone";
    private static final String GENDER = "we_gender";
    private static final String COMPANY = "we_company";
    private static final String HASHED_EMAIL = "we_hashed_email";
    private static final String HASHED_PHONE = "we_hashed_phone";
    private static final String PUSH_OPT_IN = "we_push_opt_in";
    private static final String SMS_OPT_IN = "we_sms_opt_in";
    private static final String EMAIL_OPT_IN = "we_email_opt_in";
    private static final String WHATSAPP_OPT_IN = "we_whatsapp_opt_in";
    public static final String BRIDGE_NAME = "__WEBENGAGE_MOBILE_BRIDGE__";

    Context applicationContext = null;
    private static final String TAG = "WebEngageMobileBridge";

    public WebEngageMobileBridge(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @JavascriptInterface
    public void login(String cuid) {
        Logger.d(TAG, "Bridge login called: " + cuid);
        WebEngage.get().user().login(cuid);
    }

    @JavascriptInterface
    public void logout() {
        Logger.d(TAG, "Bridge logout called");
        WebEngage.get().user().logout();
    }

    @JavascriptInterface
    public void setAttribute(String attr) {
        Logger.d(TAG, "Bridge attribute called: " + attr);
        Map<String, Object> attributes = null;
        try {
            attributes = (Map<String, Object>) DataType.convert(attr, DataType.MAP, false);
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
        try {
            if (attributes != null && attributes.size() > 0) {
                Map<String, Object> customAttributes = new HashMap<String, Object>();
                UserProfile.Builder userProfileBuilder = new UserProfile.Builder();
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value != null) {
                        if (FIRST_NAME.equals(key) && value instanceof String) {
                            userProfileBuilder.setFirstName((String) value);
                        } else if (LAST_NAME.equals(key) && value instanceof String) {
                            userProfileBuilder.setLastName((String) value);
                        } else if (EMAIL.equals(key) && value instanceof String) {
                            userProfileBuilder.setEmail((String) value);
                        } else if (BIRTH_DATE.equals(key) && value instanceof String) {
                            userProfileBuilder.setBirthDate((String) value);
                        } else if (PHONE.equals(key) && value instanceof String) {
                            userProfileBuilder.setPhoneNumber((String) value);
                        } else if (GENDER.equals(key) && value instanceof String) {
                            userProfileBuilder.setGender(Gender.valueByString((String) value));
                        } else if (COMPANY.equals(key) && value instanceof String) {
                            userProfileBuilder.setCompany((String) value);
                        } else if (HASHED_EMAIL.equals(key) && value instanceof String) {
                            userProfileBuilder.setHashedEmail((String) value);
                        } else if (HASHED_PHONE.equals(key) && value instanceof String) {
                            userProfileBuilder.setHashedPhoneNumber((String) value);
                        } else if (PUSH_OPT_IN.equals(key)) {
                            if ("true".equals(value.toString()) || "false".equals(value.toString())) {
                                userProfileBuilder.setOptIn(Channel.PUSH, Boolean.valueOf(value.toString()));
                            }
                        } else if (SMS_OPT_IN.equals(key)) {
                            if ("true".equals(value.toString()) || "false".equals(value.toString())) {
                                userProfileBuilder.setOptIn(Channel.SMS, Boolean.valueOf(value.toString()));
                            }
                        } else if (EMAIL_OPT_IN.equals(key)) {
                            if ("true".equals(value.toString()) || "false".equals(value.toString())) {
                                userProfileBuilder.setOptIn(Channel.EMAIL, Boolean.valueOf(value.toString()));
                            }
                        } else if (WHATSAPP_OPT_IN.equals(key)) {
                            if ("true".equals(value.toString()) || "false".equals(value.toString())) {
                                userProfileBuilder.setOptIn(Channel.WHATSAPP, Boolean.valueOf(value.toString()));
                            }
                        } else {
                            customAttributes.put(key, value);
                        }
                    }
                }

                if (customAttributes != null && customAttributes.size() > 0) {
                    WebEngage.get().user().setAttributes(customAttributes);
                }
                WebEngage.get().user().setUserProfile(userProfileBuilder.build());
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
    }

    @JavascriptInterface
    public void screen(String screenName, String screenData) {
        Logger.d(TAG, "Bridge screen called screenName: " + screenName + ", screenData: " + screenData);
        Map<String, Object> data = null;
        try {
            if (screenData != null) {
                data = (Map<String, Object>) DataType.convert(screenData, DataType.MAP, false);
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
        if (screenName != null) {
            if (data != null) {
                WebEngage.get().analytics().screenNavigated(screenName, data);
            } else {
                WebEngage.get().analytics().screenNavigated(screenName);
            }

        } else {
            if (data != null) {
                WebEngage.get().analytics().setScreenData(data);
            }
        }
    }

    @JavascriptInterface
    public void track(String eventName, String eventData) {
        Logger.d(TAG, "Bridge track called eventName: " + eventName + ", eventData: " + eventData);
        Map<String, Object> data = null;
        try {
            if (eventData != null) {
                data = (Map<String, Object>) DataType.convert(eventData, DataType.MAP, false);
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }

        if (eventName != null) {
            if (data != null) {
                WebEngage.get().analytics().track(eventName, data);
            } else {
                WebEngage.get().analytics().track(eventName);
            }
        }
    }
}
