package com.webengage.sdk.android;


import android.content.Context;

import com.webengage.sdk.android.utils.Gender;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

class UserImpl implements User {
    Context applicationContext = null;
    Analytics analytics = null;


    UserImpl(Context context, Analytics analytics) {
        this.applicationContext = context.getApplicationContext();
        this.analytics = analytics;
    }

    @Override
    public void setUserProfile(UserProfile userProfile) {
        if (userProfile == null) {
            return;
        }

        if (userProfile.getUserData() != null && userProfile.getUserData().size() > 0) {
            this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userProfile.getUserData(), null, null, applicationContext));
        }

        if (userProfile.getLocationData() != null && userProfile.getLocationData().size() > 1) {
            this.setLocation((double) userProfile.getLocationData().get(UserDeviceAttribute.LATITUDE.toString()), (double) userProfile.getLocationData().get(UserDeviceAttribute.LONGITUDE.toString()));
        }
    }

    @Override
    public void setEmail(String email) {
        if (!validateAttributeValue(email, UserSystemAttribute.EMAIL.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.EMAIL.toString(), email);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));

    }

    @Override
    public void setHashedEmail(String hashedEmail) {
        if (!validateAttributeValue(hashedEmail, UserSystemAttribute.HASHED_EMAIL.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.HASHED_EMAIL.toString(), hashedEmail);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setBirthDate(Integer year, Integer month, Integer day) {
        if (!validateAttributeValue(year, UserSystemAttribute.BIRTH_DATE.toString()) || !validateAttributeValue(month, UserSystemAttribute.BIRTH_DATE.toString()) || !validateAttributeValue(day, UserSystemAttribute.BIRTH_DATE.toString())) {
            return;
        }
        try {
            GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            cal.clear();
            cal.set(year, month - 1, day);
            Date birthDate = cal.getTime();

            Map<String, Object> userData = new HashMap<String, Object>();
            userData.put(UserSystemAttribute.BIRTH_DATE.toString(), birthDate);
            this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
        } catch (Exception e) {
        }
    }

    @Override
    public void setBirthDate(String birthDate) {
        if (birthDate != null && !birthDate.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = simpleDateFormat.parse(birthDate);
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(UserSystemAttribute.BIRTH_DATE.toString(), date);
                this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
            } catch (ParseException e) {

            }
        }
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (!validateAttributeValue(phoneNumber, UserSystemAttribute.PHONE.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.PHONE.toString(), phoneNumber);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setHashedPhoneNumber(String hashedPhoneNumber) {
        if (!validateAttributeValue(hashedPhoneNumber, UserSystemAttribute.HASHED_PHONE.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.HASHED_PHONE.toString(), hashedPhoneNumber);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setFirstName(String firstName) {
        if (!validateAttributeValue(firstName, UserSystemAttribute.FIRST_NAME.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.FIRST_NAME.toString(), firstName);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setLastName(String lastName) {
        if (!validateAttributeValue(lastName, UserSystemAttribute.LAST_NAME.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.LAST_NAME.toString(), lastName);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setAttribute(String attributeName, Number value) {
        if (!validateAttributeName(attributeName) || !validateAttributeValue(value, attributeName)) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(attributeName, value);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, null, userData, null, applicationContext));
    }

    @Override
    public void setAttribute(String attributeName, Date value) {
        if (!validateAttributeName(attributeName) || !validateAttributeValue(value, attributeName)) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(attributeName, value);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, null, userData, null, applicationContext));
    }

    @Override
    public void setAttribute(String attributeName, List<? extends Object> value) {
        if (!validateAttributeName(attributeName) || !validateAttributeValue(value, attributeName)) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(attributeName, value);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, null, userData, null, applicationContext));
    }


    @Override
    public void deleteAttribute(String attributeKey) {
        if (!validateAttributeName(attributeKey)) {
            return;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(attributeKey, null);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_DELETE_ATTRIBUTES, null, map, null, applicationContext));
    }

    @Override
    public void deleteAttributes(List<String> attributeKeys) {
        if (attributeKeys == null) {
            Logger.e(WebEngageConstant.TAG, "Attribute list is Invalid");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> iterator = attributeKeys.iterator();
        while (iterator.hasNext()) {
            String attributeKey = iterator.next();
            if (!validateAttributeName(attributeKey)) {
                continue;
            }
            map.put(attributeKey, null);
        }
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_DELETE_ATTRIBUTES, null, map, null, applicationContext));
    }

    @Override
    public void setAttributes(Map<String, ? extends Object> attributes) {
        if (attributes == null || attributes.size() == 0) {
            Logger.e(WebEngageConstant.TAG, "ILLEGAL ARGUMENT : attributes");
            return;
        }
        Map<String, Object> filteredMap = new HashMap<String, Object>();
        for (Map.Entry<String, ? extends Object> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            Object value = entry.getValue();
            if (!validateAttributeName(attributeName) || !validateAttributeValue(value, attributeName)) {
                continue;
            }
            filteredMap.put(attributeName, value);
        }
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, null, filteredMap, null, applicationContext));
    }

    @Override
    public void loggedIn(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            Logger.e(WebEngageConstant.TAG, "ILLEGAL ARGUMENT : User Identifier");
            return;
        }
        Map<String, Object> extraData = new HashMap<String, Object>();
        extraData.put("cuid", WebEngageUtils.truncate(identifier, 100));
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_LOGGED_IN, null, null, extraData, applicationContext));
    }

    @Override
    public void loggedOut() {
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_LOGGED_OUT, null, null, null, applicationContext));
    }

    @Override
    public void login(String identifier) {
        this.loggedIn(identifier);
    }

    @Override
    public void logout() {
        this.loggedOut();
    }

    @Override
    public void setAttribute(String attributeName, String value) {
        if (!validateAttributeName(attributeName) || !validateAttributeValue(value, attributeName)) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(attributeName, value);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, null, map, null, applicationContext));
    }

    @Override
    public void setAttribute(String attributeName, Boolean value) {
        if (!validateAttributeName(attributeName) || !validateAttributeValue(value, attributeName)) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(attributeName, value);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, null, map, null, applicationContext));
    }

    @Override
    public void setGender(Gender gender) {
        if (!validateAttributeValue(gender, UserSystemAttribute.GENDER.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.GENDER.toString(), gender.toString());
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setCompany(String company) {
        if (!validateAttributeValue(company, UserSystemAttribute.COMPANY.toString())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserSystemAttribute.COMPANY.toString(), company);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setLocation(double latitude, double longitude) {
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserDeviceAttribute.LATITUDE, latitude);
        userData.put(UserDeviceAttribute.LONGITUDE, longitude);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE_GEO_INFO, userData, null, null, applicationContext));
    }

    @Override
    public void setOptIn(Channel channel, boolean state) {
        if (!validateAttributeValue(channel, Channel.class.getSimpleName())) {
            return;
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(channel.toString(), state);

        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    @Override
    public void setDevicePushOptIn(boolean state) {
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(UserDeviceAttribute.OPT_IN_PUSH, state);
        this.analytics.dispatchEventTopic(EventFactory.newSystemEvent(EventName.USER_UPDATE, userData, null, null, applicationContext));
    }

    void generateLUID() {
        String LUID = new UUID(System.currentTimeMillis(), UUID.randomUUID().getLeastSignificantBits()).toString()/* + WebEngage.get().getWebEngageConfig().getWebEngageKey()*/;
        Logger.d(WebEngageConstant.TAG, "New luid: "+LUID);
        analytics.getPreferenceManager().saveLUID(LUID);
        CallbackDispatcher.init(applicationContext).onAnonymousIdChanged(applicationContext, LUID);
    }

    private boolean validateAttributeName(String name) {
        if (name == null || name.isEmpty()) {
            Logger.e(WebEngageConstant.TAG, "Attribute name is Invalid");
            Logger.e(WebEngageConstant.TAG, "Rejecting  user attribute : " + name);
            return false;
        }
        if (name.startsWith("we_")) {
            Logger.e(WebEngageConstant.TAG, "Found prefix \"we_\" on custom attribute name : " + name);
            Logger.e(WebEngageConstant.TAG, "Rejecting user attribute : " + name);
            return false;
        }
        return true;
    }

    private boolean validateAttributeValue(Object value, String attributeName) {
        if (value == null) {
            Logger.e(WebEngageConstant.TAG, "Found user attribute value as null");
            Logger.e(WebEngageConstant.TAG, "Rejecting user attribute : " + attributeName);
            return false;
        }
        return true;
    }


}
