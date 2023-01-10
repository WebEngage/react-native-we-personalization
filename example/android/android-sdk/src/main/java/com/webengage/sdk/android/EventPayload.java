package com.webengage.sdk.android;

import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.IMap;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventPayload implements Serializable, IMap {
    Integer id = -1;
    String interfaceId;
    String licenseCode;
    String LUID = "";
    String SUID = "";
    String CUID = "";
    String category = "";
    String eventName = "";
    Date eventTime = null;
    Map<String, Object> eventData = null;
    Map<String, Object> systemData = null;
    Map<String, Object> extraData = null;

    public EventPayload() {

    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLUID() {
        return LUID;
    }

    public void setLUID(String LUID) {
        this.LUID = LUID;
    }

    public String getSUID() {
        return SUID;
    }

    public void setSUID(String SUID) {
        this.SUID = SUID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCUID() {
        return CUID;
    }

    public void setCUID(String CUID) {
        this.CUID = CUID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventTime() {
        try {
            return (Date) DataType.cloneInternal(eventTime);
        } catch (Exception e) {
            return null;
        }
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Map<String, Object> getEventData() {
        try {
            return (Map<String, Object>) DataType.cloneInternal(eventData);
        } catch (Exception e) {
            return null;
        }
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }

    public Map<String, Object> getSystemData() {
        try {
            return (Map<String, Object>) DataType.cloneInternal(systemData);
        } catch (Exception e) {
            return null;
        }
    }

    public void setSystemData(Map<String, Object> systemData) {
        this.systemData = systemData;
    }

    public Map<String, Object> getExtraData() {
        try {
            return (Map<String, Object>) DataType.cloneInternal(extraData);
        } catch (Exception e) {
            return null;
        }
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("license_code", this.licenseCode);
        map.put("interface_id", this.interfaceId);
        map.put("suid", this.SUID);
        map.put("luid", this.LUID);
        map.put("cuid", this.CUID.isEmpty() ? null : this.CUID);
        map.put("category", this.category);
        map.put("event_name", this.eventName);
        map.put("event_time", getEventTime());
        map.put("event_data", getEventData());
        map.put("system_data", getSystemData());

        return map;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public String toString() {
        try {
            return DataType.convert(toMap(), DataType.STRING, true).toString();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public int hashCode() {
        return (this.eventName + this.LUID + this.SUID + this.CUID + this.eventTime.toString()).hashCode();
    }
}
