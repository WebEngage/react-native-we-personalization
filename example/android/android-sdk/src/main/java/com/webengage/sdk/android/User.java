package com.webengage.sdk.android;


import com.webengage.sdk.android.utils.Gender;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface User {

    void setUserProfile(UserProfile userProfile);

    void deleteAttribute(String attributeKey);

    void deleteAttributes(List<String> attributeKeys);

    void setAttribute(String attributeName, String value);

    void setAttribute(String attributeName, Boolean value);

    void setAttribute(String attributeName, Number value);

    void setAttribute(String attributeName , Date value);

    void setAttribute(String attributeName, List<? extends Object> value);

    void setAttributes(Map<String, ? extends Object> value);

    @Deprecated
    void loggedIn(String identifier);

    @Deprecated
    void loggedOut();

    void login(String identifier);

    void logout();

    void setEmail(String email);

    void setHashedEmail(String hashedEmail);

    @Deprecated
    void setBirthDate(Integer year, Integer month, Integer day);

    void setBirthDate(String birthDate);

    void setPhoneNumber(String phoneNumber);

    void setHashedPhoneNumber(String hashedPhoneNumber);

    void setGender(Gender gender);

    void setFirstName(String firstName);

    void setLastName(String lastName);

    void setCompany(String company);

    void setLocation(double latitude, double longitude);

    void setOptIn(Channel channel,boolean state);

    void setDevicePushOptIn(boolean state);
}
