package com.webengage.sdk.android;

import com.webengage.sdk.android.utils.Gender;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class UserProfile {

    private Map<String, Object> userData = null;
    private Map<String, Object> locationData = null;

    private UserProfile(Builder builder) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry : builder.userData.entrySet()) {
            if (entry.getValue() == null) {
                Logger.e(WebEngageConstant.TAG, "Illegal Argument : " + entry.getKey());
                Logger.e(WebEngageConstant.TAG, "Rejecting attribute : " + entry.getKey());
                continue;
            }
            map.put(entry.getKey(), entry.getValue());
        }

        this.userData = map;
        this.locationData = builder.locationData;
    }


    public static class Builder {
        private Map<String, Object> userData = null;
        private Map<String, Object> locationData = null;

        public Builder() {
            userData = new HashMap<String, Object>();
            locationData = new HashMap<String, Object>();
        }

        public Builder setEmail(String email) {
            userData.put(UserSystemAttribute.EMAIL.toString(), email);
            return this;
        }

        public Builder setHashedEmail(String hashedEmail) {
            userData.put(UserSystemAttribute.HASHED_EMAIL.toString(), hashedEmail);
            return this;
        }

        public Builder setBirthDate(Integer year, Integer month, Integer day) {
            try {
                GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                cal.clear();
                cal.set(year, month - 1, day);

                Date birthDate = cal.getTime();
                userData.put(UserSystemAttribute.BIRTH_DATE.toString(), birthDate);
            } catch (Exception e) {

            }

            return this;
        }

        public Builder setBirthDate(String birthDate) {
            if (birthDate != null && !birthDate.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date date = simpleDateFormat.parse(birthDate);
                    userData.put(UserSystemAttribute.BIRTH_DATE.toString(), date);
                } catch (ParseException e) {

                }
            }
            return this;

        }

        public Builder setPhoneNumber(String phoneNumber) {
            userData.put(UserSystemAttribute.PHONE.toString(), phoneNumber);
            return this;
        }

        public Builder setHashedPhoneNumber(String hashedPhoneNumber) {
            userData.put(UserSystemAttribute.HASHED_PHONE.toString(), hashedPhoneNumber);
            return this;
        }

        public Builder setGender(Gender gender) {
            userData.put(UserSystemAttribute.GENDER.toString(), gender.toString());
            return this;
        }


        public Builder setFirstName(String firstName) {
            userData.put(UserSystemAttribute.FIRST_NAME.toString(), firstName);
            return this;
        }

        public Builder setLastName(String lastName) {
            userData.put(UserSystemAttribute.LAST_NAME.toString(), lastName);
            return this;
        }

        public Builder setCompany(String company) {
            userData.put(UserSystemAttribute.COMPANY.toString(), company);
            return this;
        }

        public Builder setOptIn(Channel channel, boolean state) {
            userData.put(channel.toString(), state);
            return this;
        }

        public Builder setLocation(double latitude, double longitude) {
            locationData.put(UserDeviceAttribute.LATITUDE, latitude);
            locationData.put(UserDeviceAttribute.LONGITUDE, longitude);
            return this;
        }

        public UserProfile build() {
            UserProfile userProfile = new UserProfile(this);
            return userProfile;
        }

    }


    Map<String, Object> getUserData() {
        return this.userData;
    }

    Map<String, Object> getLocationData() {
        return this.locationData;
    }


}
