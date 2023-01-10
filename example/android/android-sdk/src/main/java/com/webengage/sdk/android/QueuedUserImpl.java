package com.webengage.sdk.android;

import com.webengage.sdk.android.utils.Gender;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by shahrukhimam on 06/10/17.
 */

class QueuedUserImpl implements User {

    Queue<Task> queue = null;

    QueuedUserImpl(Queue<Task> queue) {
        this.queue = queue;
    }

    @Override
    public void setUserProfile(UserProfile userProfile) {
        Task task = new UserTask(UserTask.SET_PROFILE, userProfile);
        this.queue.add(task);
    }

    @Override
    public void deleteAttribute(String attributeKey) {
        Task task = new UserTask(UserTask.DELETE_ATTRIBUTE_SINGLE, attributeKey);
        this.queue.add(task);
    }

    @Override
    public void deleteAttributes(List<String> attributeKeys) {
        Task task = new UserTask(UserTask.DELETE_ATTRIBUTE_MULTIPLE, attributeKeys);
        this.queue.add(task);
    }

    @Override
    public void setAttribute(String attributeName, String value) {
        Task task = new UserTask(UserTask.SET_ATTRIBUTE_STRING, attributeName, value);
        this.queue.add(task);
    }

    @Override
    public void setAttribute(String attributeName, Boolean value) {
        Task task = new UserTask(UserTask.SET_ATTRIBUTE_BOOLEAN, attributeName, value);
        this.queue.add(task);
    }

    @Override
    public void setAttribute(String attributeName, Number value) {
        Task task = new UserTask(UserTask.SET_ATTRIBUTE_NUMBER, attributeName, value);
        this.queue.add(task);
    }

    @Override
    public void setAttribute(String attributeName, Date value) {
        Task task = new UserTask(UserTask.SET_ATTRIBUTE_DATE, attributeName, value);
        this.queue.add(task);
    }

    @Override
    public void setAttribute(String attributeName, List<? extends Object> value) {
        Task task = new UserTask(UserTask.SET_ATTRIBUTE_LIST, attributeName, value);
        this.queue.add(task);
    }

    @Override
    public void setAttributes(Map<String, ? extends Object> value) {
        Task task = new UserTask(UserTask.SET_ATTRIBUTE_MAP, value);
        this.queue.add(task);
    }

    @Override
    public void loggedIn(String identifier) {
        this.login(identifier);
    }

    @Override
    public void loggedOut() {
        this.logout();
    }

    @Override
    public void login(String identifier) {
        Task task = new UserTask(UserTask.LOGIN, identifier);
        this.queue.add(task);
    }

    @Override
    public void logout() {
        Task task = new UserTask(UserTask.LOGOUT);
        this.queue.add(task);
    }

    @Override
    public void setEmail(String email) {
        Task task = new UserTask(UserTask.SET_EMAIL, email);
        this.queue.add(task);
    }

    @Override
    public void setHashedEmail(String hashedEmail) {
        Task task = new UserTask(UserTask.SET_HASHED_EMAIL, hashedEmail);
        this.queue.add(task);
    }

    @Override
    public void setBirthDate(Integer year, Integer month, Integer day) {
        Task task = new UserTask(UserTask.SET_BIRTH_DATE_INT, year, month, day);
        this.queue.add(task);
    }

    @Override
    public void setBirthDate(String birthDate) {
        Task task = new UserTask(UserTask.SET_BIRTH_DATE_STRING, birthDate);
        this.queue.add(task);
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        Task task = new UserTask(UserTask.SET_PHONE, phoneNumber);
        this.queue.add(task);
    }

    @Override
    public void setHashedPhoneNumber(String hashedPhoneNumber) {
        Task task = new UserTask(UserTask.SET_HASHED_PHONE, hashedPhoneNumber);
        this.queue.add(task);
    }

    @Override
    public void setGender(Gender gender) {
        Task task = new UserTask(UserTask.SET_GENDER, gender);
        this.queue.add(task);
    }

    @Override
    public void setFirstName(String firstName) {
        Task task = new UserTask(UserTask.SET_FIRST_NAME, firstName);
        this.queue.add(task);
    }

    @Override
    public void setLastName(String lastName) {
        Task task = new UserTask(UserTask.SET_LAST_NAME, lastName);
        this.queue.add(task);
    }

    @Override
    public void setCompany(String company) {
        Task task = new UserTask(UserTask.SET_COMPANY, company);
        this.queue.add(task);
    }

    @Override
    public void setLocation(double latitude, double longitude) {
        Task task = new UserTask(UserTask.SET_LOCATION, latitude, longitude);
        this.queue.add(task);
    }

    @Override
    public void setOptIn(Channel channel, boolean state) {
        Task task = new UserTask(UserTask.SET_OPT_IN, channel, state);
        this.queue.add(task);
    }

    @Override
    public void setDevicePushOptIn(boolean state) {
        Task task = new UserTask(UserTask.SET_DEVICE_PUSH_OPT_IN, state);
        this.queue.add(task);
    }
}
