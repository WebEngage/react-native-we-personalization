package com.webengage.sdk.android;


import com.webengage.sdk.android.utils.Gender;

import java.util.Date;
import java.util.List;
import java.util.Map;

class UserNoOpImpl implements User {
    @Override
    public void setUserProfile(UserProfile userProfile) {

    }

    @Override
    public void deleteAttribute(String attributeName) {

    }

    @Override
    public void deleteAttributes(List<String> attributeKeys) {

    }

    @Override
    public void setAttribute(String attributeName, String value) {

    }

    @Override
    public void setAttribute(String attributeName, Boolean value) {

    }

    @Override
    public void setAttribute(String attributeName, Number value) {

    }

    @Override
    public void setAttribute(String attributeName, Date value) {

    }

    @Override
    public void setAttribute(String attributeName, List<? extends Object> value) {

    }

    @Override
    public void setAttributes(Map<String, ? extends Object> value) {

    }

    @Override
    public void loggedIn(String identifier) {

    }

    @Override
    public void loggedOut() {

    }

    @Override
    public void login(String identifier) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void setEmail(String email) {

    }

    @Override
    public void setHashedEmail(String hashedEmail) {

    }

    @Override
    public void setBirthDate(Integer year, Integer month, Integer day) {

    }

    @Override
    public void setBirthDate(String birthDate) {

    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

    }

    @Override
    public void setHashedPhoneNumber(String hashedPhoneNumber) {

    }

    @Override
    public void setGender(Gender gender) {

    }

    @Override
    public void setFirstName(String firstName) {

    }

    @Override
    public void setLastName(String lastName) {

    }

    @Override
    public void setCompany(String company) {

    }

    @Override
    public void setLocation(double latitude, double longitude) {

    }

    @Override
    public void setOptIn(Channel channel, boolean state) {

    }

    @Override
    public void setDevicePushOptIn(boolean state) {

    }
}
