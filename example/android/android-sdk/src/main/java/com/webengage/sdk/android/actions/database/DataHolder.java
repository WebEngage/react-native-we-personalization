package com.webengage.sdk.android.actions.database;


import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.UserDeviceAttribute;
import com.webengage.sdk.android.UserSystemAttribute;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataHolder {
    private static DataHolder instance = null;
    public Map<String, Object> container = null;
    private List<OnDataHolderChangeListener> listeners;
    private static final Object lock = new Object();
    private List<Object> list = null;

    private DataHolder() {
        container = new HashMap<String, Object>();
        listeners = new ArrayList<OnDataHolderChangeListener>();
        list = new ArrayList<Object>();
    }

    public static DataHolder get() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DataHolder();
                }
            }
        }
        return instance;
    }

    public void registerChangeListener(OnDataHolderChangeListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<OnDataHolderChangeListener>();
        }
        this.listeners.add(listener);
    }


    private void publishChange(List<Object> key, Object value, String userIdentifier, Operation operation) {
        if (this.listeners != null) {
            for (OnDataHolderChangeListener listener : listeners) {
                listener.onChange(key, value, userIdentifier, operation);
            }
        }
    }

    public void silentSetData(String key, Object value) {
        synchronized (lock) {
            this.container.put(key, value);
        }
    }

    public void silentSetData(Map<String, Object> data) {
        synchronized (lock) {
            this.container.putAll(data);
        }
    }

    public void setData(String key, Object value) {
        synchronized (lock) {
            list.clear();
            list.add(key);
            this.setData(list, value);
        }

    }

    public void clearSessionLevelData(String userIdentifier, DataContainer dataContainer) {
        synchronized (lock) {
            Map<String, Object> deviceData = (Map<String, Object>) getData(dataContainer.toString());
            if (deviceData != null) {
                for (Map.Entry<String, Object> entry : deviceData.entrySet()) {
                    String key = entry.getKey();
                    if (key != null && key.endsWith("_session")) {
                        setOrUpdateUserProfile(userIdentifier, key, null, dataContainer);
                    }
                }
            }
        }
    }


    public void clearScreenEvents() {
        synchronized (lock) {
            Map<String, Object> events = (Map<String, Object>) getData(DataContainer.EVENT.toString());
            Map<String, Object> sessionEvents = null;
            if (events != null) {
                sessionEvents = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : events.entrySet()) {
                    if (entry.getKey().equals(EventName.WE_WK_SESSION_DELAY.toString())) {
                        sessionEvents.put(EventName.WE_WK_SESSION_DELAY.toString(), entry.getValue());
                    }
                }
            }
            list.clear();
            list.add(DataContainer.EVENT.toString());
            this.setData(list, sessionEvents);
        }
    }


    public void clearAllEvents() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.EVENT.toString());
            this.setData(list, null);
        }
    }

    public void clearScreenData() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            this.setData(list, null);
        }
    }


    protected void setOrUpdateUserProfile(String userIdentifier, String attributeKey, Object value, DataContainer dataContainer, Operation operation) {
        synchronized (lock) {
            list.clear();
            list.add(dataContainer.toString());
            list.add(attributeKey);
            setData(list, value, userIdentifier, operation);
        }
    }


    public void setOrUpdateUserProfile(String userIdentifier, String attributeKey, Object value, DataContainer dataContainer) {
        synchronized (lock) {
            this.setOrUpdateUserProfile(userIdentifier, attributeKey, value, dataContainer, Operation.FORCE_UPDATE);
        }
    }

    protected void setOrUpdateUsersSystemAttributes(String userIdentifier, Map<String, Object> attributes) {
        synchronized (lock) {
            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    if (UserSystemAttribute.valueByString(key) != null) {
                        setOrUpdateUserProfile(userIdentifier, key, entry.getValue(), DataContainer.USER);
                    }
                    if (UserDeviceAttribute.isDeviceAttribute(key)) {
                        setOrUpdateUserProfile(userIdentifier, key, entry.getValue(), DataContainer.ANDROID);
                    }
                }
            }
        }
    }

    protected void setOrUpdateUsersDeviceAttributes(String userIdentifier, Map<String, Object> attributes) {
        synchronized (lock) {
            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    setOrUpdateUserProfile(userIdentifier, key, entry.getValue(), DataContainer.ANDROID);

                }
            }
        }
    }

    protected void setOrUpdateUsersCustomAttributes(String userIdentifier, Map<String, Object> attributes) {
        synchronized (lock) {
            if (attributes != null) {
                for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                    String key = entry.getKey();
                    setOrUpdateUserProfile(userIdentifier, key, entry.getValue(), DataContainer.ATTR);

                }
            }
        }
    }

    public void setOrUpdateEventCriteriaValue(String userIdentifier, String criteriaId, Map<String, Object> value) {
        synchronized (lock) {
            setOrUpdateUserProfile(userIdentifier, criteriaId, value, DataContainer.EVENT_CRITERIA);
        }
    }

    protected void setOrUpdateEventAttributes(String eventName, Map<String, Object> attributes) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.EVENT.toString());
            list.add(eventName);
            setData(list, attributes);
        }
    }

    protected void setOrUpdateLatestEventCache(String eventName, Map<String, Object> attributes) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.LATEST_EVENT.toString());
            list.add(eventName);
            setData(list, attributes);
        }
    }


    public void setSystemScreenData(Map<String, Object> data) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.SYSTEM);
            setData(list, data);
        }
    }

    protected void setCustomScreenData(Map<String, Object> data) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.CUSTOM);
            setData(list, data);
        }
    }


    protected void incrementUserProfile(String userIdentifier, String attributeKey, Number v, DataContainer dataContainer) {
        synchronized (lock) {
            list.clear();
            list.add(dataContainer.toString());
            list.add(attributeKey);
            Number value = (Number) getData(list);
            if (value == null) {
                value = 0;
            }
            value = value.doubleValue() + v.doubleValue();
            try {
                value = (Number) DataType.convert(value, DataType.detect(v), false);
                setData(list, value, userIdentifier, Operation.INCREMENT);
            } catch (Exception e) {
                setData(list, value, userIdentifier, Operation.INCREMENT);
            }


        }
    }

    protected void incrementUsersSystemAttributes(String userIdentifier, Map<String, Object> attributes) {
        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String key = entry.getKey();
                if (UserSystemAttribute.valueByString(key) != null) {
                    incrementUserProfile(userIdentifier, key, (Number) entry.getValue(), DataContainer.USER);
                }
                if (UserDeviceAttribute.isDeviceAttribute(key)) {
                    incrementUserProfile(userIdentifier, key, (Number) entry.getValue(), DataContainer.ANDROID);
                }
            }
        }
    }

    protected void incrementUsersCustomAttributes(String userIdentifier, Map<String, Object> attributes) {
        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                incrementUserProfile(userIdentifier, entry.getKey(), (Number) entry.getValue(), DataContainer.ATTR);
            }
        }
    }


    public void setEntityRunningState(boolean value) {
        AtomicBoolean atomicBoolean = (AtomicBoolean) getData("entity_is_running");
        if (atomicBoolean == null) {
            atomicBoolean = new AtomicBoolean(false);
        }
        atomicBoolean.set(value);
        silentSetData("entity_is_running", atomicBoolean);
    }

    public boolean compareAndSetEntityRunningState(boolean expected, boolean actual) {
        AtomicBoolean atomicBoolean = (AtomicBoolean) getData("entity_is_running");
        if (atomicBoolean == null) {
            atomicBoolean = new AtomicBoolean(false);
        }
        boolean result = atomicBoolean.compareAndSet(expected, actual);
        silentSetData("entity_is_running", atomicBoolean);
        return result;
    }

    /**
     * Responsible for most of my hair falls.
     *
     * @param key
     * @param value
     * @param userIdentifier
     * @param operation
     */
    private void setData(List<Object> key, Object value, String userIdentifier, Operation operation) {
        synchronized (lock) {
            Object container = this.container;
            boolean isDataChanged = false;
            for (int i = 0; i < key.size(); i++) {
                Object data = null;
                if (container instanceof Map) {
                    data = ((Map<String, Object>) container).get(key.get(i).toString());
                } else if (container instanceof List) {
                    Integer index = Integer.parseInt(key.get(i).toString());
                    if (index < ((List<Object>) container).size()) {
                        data = ((List<Object>) container).get(index);
                    }
                }
                if (i == key.size() - 1) {
                    if (!Operation.OPT_UPDATE.equals(operation) || data == null) {
                        if (container instanceof Map) {
                            ((Map<String, Object>) container).put(key.get(i).toString(), value);
                        } else if (container instanceof List) {
                            List<Object> list = (List<Object>) container;
                            Integer index = Integer.parseInt(key.get(i).toString());
                            if (list.size() > index) {
                                list.set(index, value);
                            } else {
                                for (int j = list.size() + 1; j <= index + 1; j++) {
                                    list.add(null);
                                }
                                list.set(index, value);

                            }
                        }
                        isDataChanged = true;
                    }
                } else {
                    if (data == null) {
                        if (i + 1 < key.size() && key.get(i + 1) instanceof Number) {
                            data = new ArrayList<Object>();
                        } else {
                            data = new HashMap<String, Object>();
                        }
                        if (container instanceof Map) {
                            ((Map<String, Object>) container).put(key.get(i).toString(), data);
                        } else if (container instanceof List) {
                            List<Object> list = (List<Object>) container;
                            Integer index = Integer.parseInt(key.get(i).toString());
                            if (list.size() > index) {
                                list.set(index, data);
                            } else {
                                for (int j = list.size() + 1; j <= index + 1; j++) {
                                    list.add(null);
                                }
                                list.set(index, data);
                            }
                        }

                    }
                    if (container instanceof Map) {
                        container = ((Map<String, Object>) container).get(key.get(i).toString());
                    } else if (container instanceof List) {
                        container = ((List<Object>) container).get(Integer.parseInt(key.get(i).toString()));
                    }

                }

            }
            if (isDataChanged) {
                publishChange(key, value, userIdentifier, operation);
            }

        }
    }

    private void setData(List<Object> key, Object value, Operation operation) {
        this.setData(key, value, null, operation);
    }

    public void setData(List<Object> key, Object value) {
        this.setData(key, value, null, Operation.FORCE_UPDATE);
    }

    /**
     * Alert!!!!! Not for weak hearted.
     *
     * @param keys
     * @return
     */
    public Object getData(List<? extends Object> keys) {
        if (keys == null) {
            return null;
        }
        Object data = null;
        synchronized (lock) {
            if (container.containsKey(keys.get(0).toString())) {
                data = container.get(keys.get(0).toString());
            }

            for (int i = 1; i < keys.size(); i++) {
                if (data != null) {
                    if (data instanceof Map<?, ?>) {
                        Map<String, Object> map = (Map<String, Object>) data;
                        data = null;
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equalsIgnoreCase(keys.get(i).toString())) {
                                data = entry.getValue();
                                break;
                            }
                        }
                        continue;
                    } else if (data instanceof List<?>) {
                        List<Object> list = (List<Object>) data;
                        data = null;
                        if (keys.get(i) != null) {
                            if (list.size() > Integer.parseInt(keys.get(i).toString())) {
                                data = list.get(Integer.parseInt(keys.get(i).toString()));
                            }
                        }
                        continue;
                    } else {
                        data = null;
                    }
                }

            }
        }


        try {
            return DataType.cloneInternal(data);//should be false to handle other unknown data types such as AtomicBoolean
        } catch (Exception e) {
            return null;
        }


    }

    public Object getData(String key) {
        Object data;
        synchronized (lock) {
            data = container.get(key);
        }
        try {
            return DataType.cloneInternal(data);
        } catch (Exception e) {
            return null;
        }
    }


    public String getLatestSessionType() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("session_type");
            return (String) getData(list);
        }
    }

    public Integer getSDKId() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("sdk_id");
            return (Integer) getData(list);
        }
    }

    public Double getSDKVersion() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("sdk_version");
            return (Double) getData(list);
        }
    }

    public Double getLatitude() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.LATITUDE.toString());
            return (Double) getData(list);
        }
    }


    public Double getLongitude() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.LONGITUDE.toString());
            return (Double) getData(list);
        }
    }


    public String getCity() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.CITY.toString());
            return (String) getData(list);
        }
    }


    public String getCountry() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.COUNTRY.toString());
            return (String) getData(list);
        }
    }

    public String getRegion() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.REGION.toString());
            return (String) getData(list);
        }
    }

    public String getLocality() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.LOCALITY.toString());
            return (String) getData(list);
        }
    }

    public String getPostalCode() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.POSTAL_CODE.toString());
            return (String) getData(list);
        }
    }

    public Long getTotalPageViewCount() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("total_page_view_count");
            return (Long) getData(list);
        }
    }

    public Long getSessionPageViewCount() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("page_view_count_session");
            return (Long) getData(list);
        }
    }

    public Long getForegroundSessionCount() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("session_count");
            return (Long) getData(list);
        }
    }

    public Long getBackgroundSessionCount() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add("b_session_count");
            return (Long) getData(list);
        }
    }

    public String getScreenPath() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.SYSTEM);
            list.add("screen_path");
            return (String) getData(list);
        }
    }

    public String getScreenTitle() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.SYSTEM);
            list.add("screen_title");
            return (String) getData(list);
        }
    }

    public String getScreenName() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.SYSTEM);
            list.add("screen_name");
            return (String) getData(list);
        }
    }

    public Long getEntityTotalViewCountPerScope(Map<String, Object> entityObj, WebEngageConstant.Entity entity) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(getScopeStringForExperiment(entityObj, entity) + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }

    public Long getEntityTotalViewCountAcrossScopes(String experimentId) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(experimentId + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }

    public Long getEntityTotalViewCountInSessionAcrossScopes(String expermimentId) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(expermimentId + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW_SESSION);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }


    public Long getEntityTotalCloseCountInSessionPerScope(Map<String, Object> entityObj, WebEngageConstant.Entity entity) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(getScopeStringForExperiment(entityObj, entity) + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLOSE_SESSION);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }

    public Long getEntityTotalHideCountInSessionPerScope(Map<String, Object> entityObj, WebEngageConstant.Entity entity) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(getScopeStringForExperiment(entityObj, entity) + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.HIDE_SESSION);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }


    public Long getEntityTotalCloseCountPerScope(Map<String, Object> entityObj, WebEngageConstant.Entity entity) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(getScopeStringForExperiment(entityObj, entity) + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLOSE);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }

    public Long getEntityTotalHideCountPerScope(Map<String, Object> entityObj, WebEngageConstant.Entity entity) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.SCOPES.toString());
            list.add(getScopeStringForExperiment(entityObj, entity) + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.HIDE);
            Long result = (Long) getData(list);
            return result == null ? 0l : result;
        }
    }

    public Long getEntityTotalClickCountPerScope(Map<String, Object> entityObj, WebEngageConstant.Entity entity) {
        list.clear();
        list.add(DataContainer.SCOPES.toString());
        list.add(getScopeStringForExperiment(entityObj, entity) + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLICK);
        Long result = (Long) getData(list);
        return result == null ? 0l : result;
    }

    public Map<String, Object> getCustomScreenData() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.CUSTOM);
            return (Map<String, Object>) getData(list);
        }
    }

    public Map<String, Object> getSystemScreenData() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.PAGE.toString());
            list.add(WebEngageConstant.SYSTEM);
            return (Map<String, Object>) getData(list);
        }
    }

    public Date getUserLastSeen() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.LAST_SEEN.toString());
            return (Date) getData(list);
        }
    }

    public Map<String, Object> getEventCriteria(String id) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.EVENT_CRITERIA.toString());
            list.add(id);
            return (Map<String, Object>) getData(list);
        }
    }

    public Object getDeviceData(String key) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(key);
            return getData(list);
        }
    }

    public Object getUserSystemData(String key) {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.USER.toString());
            list.add(key);
            return getData(list);
        }
    }

    public Date getUserLastLoggedIn() {
        synchronized (lock) {
            list.clear();
            list.add(DataContainer.ANDROID.toString());
            list.add(UserDeviceAttribute.LAST_LOGGED_IN.toString());
            return (Date) getData(list);
        }
    }

    public boolean isAppForeground() {
        synchronized (lock) {
            return this.container.get("app_foreground") != null && (boolean) this.container.get("app_foreground");
        }
    }

    public void setAppForeground(boolean value) {
        synchronized (lock) {
            this.container.put("app_foreground", value);
        }
    }

    public boolean isBootUpCalled() {
        synchronized (lock) {
            return this.container.get("boot_up") != null && (boolean) this.container.get("boot_up");
        }
    }

    public void setBootUpCalled(boolean value) {
        synchronized (lock) {
            this.container.put("boot_up", value);
        }
    }

    public void setFirstActivityStartTime(long epoch) {
        synchronized (lock) {
            this.container.put("f_activity_start_ep", epoch);
        }
    }

    public long getFirstActivityStartEpoch() {
        synchronized (lock) {
            return (this.container.containsKey("f_activity_start_ep") ? (long) this.container.get("f_activity_start_ep") : -1);
        }
    }

    public Long getTZO() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("tzo");
            return (Long) getData(list);
        }
    }

    public Long getJCXInterval() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("jcxPollTime");
            return (Long) getData(list);
        }
    }


    public boolean useLegacyRuleCompiler() {
        synchronized (lock) {
            return this.container.get("useLegacyRuleCompiler") != null && (boolean) this.container.get("useLegacyRuleCompiler");
        }
    }


    public void setUseLegacyRuleCompiler(boolean useLegacyRuleCompiler) {
        synchronized (lock) {
            this.container.put("useLegacyRuleCompiler", useLegacyRuleCompiler);
        }
    }

    public List<Object> getPageDelayValues() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("events");
            list.add(EventName.WE_WK_PAGE_DELAY.toString());
            return (List<Object>) getData(list);
        }
    }

    public String getBaseUrl() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("gbp");
            return (String) getData(list);
        }
    }

    public Map<String, List<Object>> getTokens() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("tokens");
            return (Map<String, List<Object>>) getData(list);
        }
    }

    public Map<String, Object> getUpfc() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("upfc");
            return (Map<String, Object>) getData(list);
        }
    }

    public Map<String, Object> getGeoFences() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("geoFences");
            return (Map<String, Object>) getData(list);
        }
    }

    public List<Object> getSessionDelayValues() {
        synchronized (lock) {
            list.clear();
            list.add(WebEngageConstant.CONFIG);
            list.add("events");
            list.add(EventName.WE_WK_SESSION_DELAY);
            return (List<Object>) getData(list);
        }
    }

    public boolean getOptInValueForEntity(WebEngageConstant.Entity type) {
        synchronized (lock) {
            if (type != null) {
                switch (type) {
                    case NOTIFICATION:
                        Boolean deviceOptIn = (Boolean) getDeviceData(UserDeviceAttribute.OPT_IN_INAPP);
                        return deviceOptIn == null ? true : deviceOptIn;

                    case PUSH:
                        Boolean userOptIn = (Boolean) getUserSystemData(UserSystemAttribute.PUSH_OPT_IN.toString());
                        userOptIn = (userOptIn == null) ? true : userOptIn;

                        deviceOptIn = (Boolean) getDeviceData(UserDeviceAttribute.OPT_IN_PUSH);
                        deviceOptIn = (deviceOptIn == null) ? true : deviceOptIn;
                        return userOptIn && deviceOptIn;

                    default:
                        return true;
                }
            } else {
                return true;
            }
        }
    }

    public String getScopeStringForExperiment(Map<String, Object> entityObj, WebEngageConstant.Entity type) {
        synchronized (lock) {
            String journeyId = (String) entityObj.get("journeyId");
            String experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(0).entityExperimentIdKey;
            switch (type) {
                case INLINE_PERSONALIZATION:
                    experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(0).entityExperimentIdKey;
                    break;
                case NOTIFICATION:
                    experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(1).entityExperimentIdKey;
                    break;
                case SURVEY:
                    experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(2).entityExperimentIdKey;
                    break;
            }
            String experimentId = (String) entityObj.get(experimentIdKey);
            StringBuilder sb = new StringBuilder();
            sb.append(experimentId);
            if (journeyId != null) {
                List<Object> list = new ArrayList<Object>();
                list.add("journey");
                list.add(journeyId);
                list.add("id");
                String instanceId = (String) getData(list);
                if (instanceId != null) {
                    sb.append("[");
                    sb.append(instanceId);
                    sb.append("]");
                }
            }
            return sb.toString();
        }
    }

    public Map<String, Object> getInlineCampaignsData() {
        synchronized (lock) {
            return container.get("inline_campaigns") == null ? null
                    : (Map<String, Object>) container.get("inline_campaigns");
        }
    }

    public Map<String, Object> getInAppCampaignsData() {
        synchronized (lock) {
            return container.get("in_app_campaigns") == null ? null
                    : (Map<String, Object>) container.get("in_app_campaigns");
        }
    }

    public List<HashMap<String, Object>> getInlineProperties() {
        synchronized (lock) {
            return container.get("inline_properties") == null ? null
                    : (List<HashMap<String, Object>>) container.get("inline_properties");
        }
    }

    public int getIPCampaignLimit() {
        synchronized (lock) {
            return container.get("p_campaign_limit") == null ? WebEngageConstant.I_P_CAMPAIGN_LIMIT
                    : Integer.parseInt(String.valueOf((Long)container.get("p_campaign_limit")));
        }
    }

    public boolean isConfigUpdated() {
        synchronized (lock) {
            return this.container.get(WebEngageConstant.REFRESH_CONFIG_RULE) != null && (boolean) this.container.get(WebEngageConstant.REFRESH_CONFIG_RULE);
        }
    }


}
