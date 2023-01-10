package com.webengage.sdk.android;


import com.webengage.sdk.android.utils.Gender;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.Date;
import java.util.List;
import java.util.Map;

class UserTask implements Task<User> {

    private int task = -1;
    private Object[] args = null;
    protected static final int SET_PROFILE = 0;
    protected static final int SET_EMAIL = 1;
    protected static final int SET_HASHED_EMAIL = 2;
    protected static final int SET_BIRTH_DATE_INT = 3;
    protected static final int SET_BIRTH_DATE_STRING = 4;
    protected static final int SET_PHONE = 5;
    protected static final int SET_HASHED_PHONE = 6;
    protected static final int SET_FIRST_NAME = 7;
    protected static final int SET_LAST_NAME = 8;
    protected static final int SET_GENDER = 9;
    protected static final int SET_COMPANY = 10;
    protected static final int SET_ATTRIBUTE_DATE = 13;
    protected static final int SET_ATTRIBUTE_LIST = 14;
    protected static final int SET_ATTRIBUTE_MAP = 15;
    protected static final int SET_ATTRIBUTE_STRING = 16;
    protected static final int SET_ATTRIBUTE_BOOLEAN = 17;
    protected static final int DELETE_ATTRIBUTE_SINGLE = 19;
    protected static final int DELETE_ATTRIBUTE_MULTIPLE = 20;
    protected static final int LOGIN = 21;
    protected static final int LOGOUT = 22;
    protected static final int SET_OPT_IN = 23;
    protected static final int SET_LOCATION = 24;
    protected static final int SET_ATTRIBUTE_NUMBER = 25;
    protected static final int SET_DEVICE_PUSH_OPT_IN = 26;

    UserTask(int task, Object... args) {
        this.task = task;
        this.args = args;
    }

    @Override
    public void execute(User user) {
        try {
            switch (this.task) {
                case SET_PROFILE:
                    if (this.args != null && this.args.length > 0) {
                        user.setUserProfile((UserProfile) this.args[0]);
                    }
                    break;

                case SET_EMAIL:
                    if (this.args != null && this.args.length > 0) {
                        user.setEmail((String) this.args[0]);
                    }
                    break;
                case SET_HASHED_EMAIL:
                    if (this.args != null && this.args.length > 0) {
                        user.setHashedEmail((String) this.args[0]);
                    }
                    break;

                case SET_BIRTH_DATE_INT:
                    if (this.args != null && this.args.length > 2) {
                        user.setBirthDate((Integer) this.args[0], (Integer) this.args[1], (Integer) this.args[2]);
                    }
                    break;

                case SET_BIRTH_DATE_STRING:
                    if (this.args != null && this.args.length > 0) {
                        user.setBirthDate((String) this.args[0]);
                    }
                    break;

                case SET_PHONE:
                    if (this.args != null && this.args.length > 0) {
                        user.setPhoneNumber((String) this.args[0]);
                    }
                    break;

                case SET_HASHED_PHONE:
                    if (this.args != null && this.args.length > 0) {
                        user.setHashedPhoneNumber((String) this.args[0]);
                    }
                    break;
                case SET_FIRST_NAME:
                    if (this.args != null && this.args.length > 0) {
                        user.setFirstName((String) this.args[0]);
                    }
                    break;
                case SET_LAST_NAME:
                    if (this.args != null && this.args.length > 0) {
                        user.setLastName((String) this.args[0]);
                    }
                    break;
                case SET_GENDER:
                    if (this.args != null && this.args.length > 0) {
                        user.setGender((Gender) this.args[0]);
                    }
                    break;

                case SET_COMPANY:
                    if (this.args != null && this.args.length > 0) {
                        user.setCompany((String) this.args[0]);
                    }
                    break;

                case SET_LOCATION:
                    if (this.args != null && this.args.length > 1 && this.args[0] != null && this.args[1] != null) {
                        user.setLocation((double) this.args[0], (double) this.args[1]);
                    }
                    break;

                case SET_ATTRIBUTE_NUMBER:
                    if (this.args != null && this.args.length > 1) {
                        user.setAttribute((String) this.args[0], (Number) this.args[1]);
                    }
                    break;

                case SET_ATTRIBUTE_DATE:
                    if (this.args != null && this.args.length > 1) {
                        user.setAttribute((String) this.args[0], (Date) this.args[1]);
                    }
                    break;
                case SET_ATTRIBUTE_LIST:
                    if (this.args != null && this.args.length > 1) {
                        user.setAttribute((String) this.args[0], (List<Object>) this.args[1]);
                    }
                    break;

                case SET_ATTRIBUTE_MAP:
                    if (this.args != null && this.args.length > 0) {
                        user.setAttributes((Map<String, Object>) this.args[0]);
                    }
                    break;
                case SET_ATTRIBUTE_STRING:
                    if (this.args != null && this.args.length > 1) {
                        user.setAttribute((String) this.args[0], (String) this.args[1]);
                    }
                    break;
                case SET_ATTRIBUTE_BOOLEAN:
                    if (this.args != null && this.args.length > 1) {
                        user.setAttribute((String) this.args[0], (Boolean) this.args[1]);
                    }
                    break;

                case DELETE_ATTRIBUTE_SINGLE:
                    if (this.args != null && this.args.length > 0) {
                        user.deleteAttribute((String) this.args[0]);
                    }
                    break;
                case DELETE_ATTRIBUTE_MULTIPLE:
                    if (this.args != null && this.args.length > 0) {
                        user.deleteAttributes((List<String>) this.args[0]);
                    }
                    break;

                case LOGIN:
                    if (this.args != null && this.args.length > 0) {
                        user.login((String) this.args[0]);
                    }
                    break;

                case LOGOUT:
                    user.logout();
                    break;

                case SET_OPT_IN:
                    if (this.args != null && this.args.length > 1) {
                        user.setOptIn((Channel) this.args[0], (boolean) this.args[1]);
                    }
                    break;

                case SET_DEVICE_PUSH_OPT_IN:
                    if (this.args != null && this.args.length > 0) {
                        user.setDevicePushOptIn((boolean) this.args[0]);
                    }
                    break;
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred while executing queued task of User: " + e.toString());
        }
    }
}
