package com.webengage.sdk.android.actions.database;


import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.UserDeviceAttribute;
import com.webengage.sdk.android.UserSystemAttribute;
import com.webengage.sdk.android.utils.Gender;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DataHolderTest {

    static String userIdentifier = "shahrukh@gmail.com";
    static Map<String, Object> pageCustomData;
    static Map<String, Object> pageSystemData;
    static Map<String, Object> eventCriteria1;
    static Map<String, Object> eventCriteria2;
    static Map<String, Object> userSystemAttribute;
    static Map<String, Object> userCustomAttribute;
    static Map<String, Object> userDeviceAttribute;
    static List<Object> path;
    static Map<String, Object> systemEventAttributes;
    static Map<String, Object> customEventAttributes;
    static Map<String, Object> internalEventAttributes;

    @BeforeClass
    public static void setUp() {
        clearDataHolder();

        userSystemAttribute = new HashMap<String, Object>();
        userSystemAttribute.put(UserSystemAttribute.FIRST_NAME.toString(), "shahrukh");
        userSystemAttribute.put(UserSystemAttribute.PHONE.toString(), "8092377032");
        userSystemAttribute.put(UserSystemAttribute.TIME_SPENT.toString(), 60000l);
        userSystemAttribute.put(UserSystemAttribute.CITY.toString(), "Mumbai");
        userSystemAttribute.put(UserSystemAttribute.REFERRER.toString(), "direct");


        eventCriteria1 = new HashMap<String, Object>();
        eventCriteria1.put("criteria_id", "id1");
        eventCriteria1.put("function", "SUM");
        eventCriteria1.put("val", 100);

        eventCriteria2 = new HashMap<String, Object>();
        eventCriteria2.put("criteria_id", "id2");
        eventCriteria2.put("function", "AVG");
        eventCriteria2.put("count", 3);
        eventCriteria2.put("val", 900);

        pageSystemData = new HashMap<String, Object>();
        pageSystemData.put("screen_name", "ProductScreen");
        pageSystemData.put("screen_path", "com.kp.lr.ProductScreen");
        pageSystemData.put("screen_title", "Products");

        pageCustomData = new HashMap<String, Object>();
        pageCustomData.put("product", "shoes");
        pageCustomData.put("discount", true);

        userCustomAttribute = new HashMap<String, Object>();
        userCustomAttribute.put("isMarried", true);
        userCustomAttribute.put("luckyNumber", 101);

        userDeviceAttribute = new HashMap<String, Object>();
        userDeviceAttribute.put("opt_in_push", true);
        userDeviceAttribute.put("manufacturer", "Sony");
        userDeviceAttribute.put("session_count", 1l);

        path = new ArrayList<Object>();
        path.add("page");
        path.add("custom");
        path.add("lastItemViewed");

        systemEventAttributes = new HashMap<String, Object>();
        Map<String, Object> notificationClickSystemAttributes = new HashMap<String, Object>();
        notificationClickSystemAttributes.put("id", "~id1");
        notificationClickSystemAttributes.put("experiment_id", "~exp1");
        notificationClickSystemAttributes.put("call_to_action", "~cta1");
        notificationClickSystemAttributes.put("brand", "Sony");
        notificationClickSystemAttributes.put("city", "Mumbai");
        systemEventAttributes.put("we_wk_sys", notificationClickSystemAttributes);
        //page data
        systemEventAttributes.put("product", "shoes");
        systemEventAttributes.put("discount", true);

        customEventAttributes = new HashMap<String, Object>();
        Map<String, Object> addtocartSystemAttr = new HashMap<String, Object>();
        addtocartSystemAttr.put("brand", "Sony");
        addtocartSystemAttr.put("city", "Mumbai");
        customEventAttributes.put("we_wk_sys", addtocartSystemAttr);
        customEventAttributes.put("product_id", "45001");
        customEventAttributes.put("category", "fragrance");
        customEventAttributes.put("price", 12.80);


        internalEventAttributes = new HashMap<String, Object>();
        internalEventAttributes.put("value", 30001);

    }

    private static void clearDataHolder() {
        DataHolder dataHolder = DataHolder.get();
        if (!dataHolder.container.isEmpty()) {
            try {
                Field instance = dataHolder.getClass().getDeclaredField("instance");
                instance.setAccessible(true);
                instance.set(null, null);
            } catch (Throwable t) {
                System.out.println("Exception while clearing DataHolder: " + t.getMessage());
            }
        }
    }

    @Test
    public void testAllMethodsSequentially() {

        //test all set methods
        testSetDataWithKeyAsString();
        testSetDataWithKeyAsList();
        testSetOrUpdateUsersSystemAttributes();
        testSetOrUpdateEventCriteriaValue();
        testSetSystemScreenData();
        testSetCustomScreenData();
        testSetOrUpdateUsersCustomAttribute();
        testSetOrUpdateUsersDeviceAttribute();
        testSetOrUpdateUserProfileWithoutOperation();
        testSetOrUpdateUserProfileWithOpeartion();
        testIncrementUserProfile();
        testIncrementUserSystemAttribute();
        testIncrementUserCustomAttribute();
        testSetOrUpdateEventAttributes();

        //test all get methods
        testGetDataWithStringAsKey();
        testGetDataWithListAsKey();
        testGetDataForImmutability();
        beginAllGetMethodsTest();
        testGetLatestSessionType();
        testGetSDKId();
        testGetSDKVersion();
        testGetLatitude();
        testGetLongitude();
        testGetCity();
        testGetCountry();
        testGetRegion();
        testGetLocality();
        testGetPostalCode();
        testGetTotalPageViewCount();
        testGetSessionPageViewCount();
        testGetForegroundSessionCount();
        testGetbackgroundSessionCount();
        testGetScreenPath();
        testGetScreenTitle();
        testGetScreenName();
        testGetEntityTotalViewCountPerScope();
        testGetEntityTotalViewCountAcrossScope();
        testGetEntityTotalViewCountInSessionAcrossScopes();
        testGetEntityTotalCloseCountPerScope();
        testGetEntityTotalCloseCountInSessionPerScope();
        testGetEntityTotalHideCountPerScope();
        testGetEntityTotalHideCountInSessionPerScope();
        testGetEntityTotalClickCountPerScope();

        testGetCustomScreenData();
        testGetSystemScreenData();
        testGetUserLastSeen();
        testGetEventCriteria();
        testGetDeviceData();
        testGetUserSystemData();
        testGetUserLastLoggedIn();
        testIsAppForeground();
        testGetTZO();
        testGetPageDelayValues();
        testGetSessionDelayValues();
        testGetBaseUrl();
        testGetOptInValue();

        //test all clear methods
        testClearSessionDeviceData();
        testClearScreenEvents();
        testClearAllEvents();
        testClearScreenData();

        testForListIndex();
    }


    public void testSetDataWithKeyAsString() {
        DataHolder.get().setData("x", 1);
        assertEquals(1, DataHolder.get().container.get("x"));
    }


    public void testSetDataWithKeyAsList() {
        DataHolder.get().setData(path, "watch");
        Map<String, Object> pageData = getPageData();
        assertNotNull(pageData);

        Map<String, Object> customData = (Map<String, Object>) pageData.get(WebEngageConstant.CUSTOM);
        assertNotNull(customData);
        assertEquals("watch", customData.get("lastItemViewed"));
    }


    public void testSetOrUpdateUsersSystemAttributes() {
        DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, userSystemAttribute);
        Map<String, Object> userData = getUserSystemData();
        Map<String, Object> deviceData = getDeviceData();
        assertNotNull(userData);
        assertNotNull(deviceData);
        assertEquals(5, userData.size());
        assertEquals(3, deviceData.size());
        assertEquals("shahrukh", userData.get(UserSystemAttribute.FIRST_NAME.toString()));
        assertEquals("8092377032", userData.get(UserSystemAttribute.PHONE.toString()));
        assertEquals(60000l, userData.get(UserSystemAttribute.TIME_SPENT.toString()));
        assertEquals(60000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", userData.get(UserSystemAttribute.CITY.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", userData.get(UserSystemAttribute.REFERRER.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
    }


    public void testSetOrUpdateEventCriteriaValue() {
        DataHolder.get().setOrUpdateEventCriteriaValue(userIdentifier, "id1", eventCriteria1);
        DataHolder.get().setOrUpdateEventCriteriaValue(userIdentifier, "id2", eventCriteria2);
        Map<String, Object> eventCriterias = getEventCriterias();
        assertNotNull(eventCriterias);
        Map<String, Object> ec1 = (Map<String, Object>) eventCriterias.get("id1");
        assertNotNull(ec1);
        assertEquals(3, ec1.size());
        assertEquals("id1", ec1.get("criteria_id"));
        assertEquals("SUM", ec1.get("function"));
        assertEquals(100, ec1.get("val"));

        Map<String, Object> ec2 = (Map<String, Object>) eventCriterias.get("id2");
        assertNotNull(ec2);
        assertEquals(4, ec2.size());
        assertEquals("id2", ec2.get("criteria_id"));
        assertEquals("AVG", ec2.get("function"));
        assertEquals(900, ec2.get("val"));
        assertEquals(3, ec2.get("count"));
    }


    public void testSetSystemScreenData() {
        DataHolder.get().setSystemScreenData(pageSystemData);
        Map<String, Object> pageData = getPageData();
        assertNotNull(pageData);
        assertEquals(2, pageData.size());
        Map<String, Object> systemData = (Map<String, Object>) pageData.get(WebEngageConstant.SYSTEM);
        assertNotNull(systemData);
        assertEquals("ProductScreen", systemData.get("screen_name"));
        assertEquals("com.kp.lr.ProductScreen", systemData.get("screen_path"));
        assertEquals("Products", systemData.get("screen_title"));

        Map<String, Object> customData = (Map<String, Object>) pageData.get(WebEngageConstant.CUSTOM);
        assertNotNull(customData);
        assertEquals("watch", customData.get("lastItemViewed"));


    }


    public void testSetCustomScreenData() {
        DataHolder.get().setCustomScreenData(pageCustomData);
        Map<String, Object> pageData = getPageData();
        assertNotNull(pageData);
        assertEquals(2, pageData.size());
        Map<String, Object> customData = (Map<String, Object>) pageData.get(WebEngageConstant.CUSTOM);
        assertNotNull(customData);
        assertEquals(2, customData.size());
        assertEquals("shoes", customData.get("product"));
        assertTrue((boolean) customData.get("discount"));

        Map<String, Object> systemData = (Map<String, Object>) pageData.get(WebEngageConstant.SYSTEM);
        assertNotNull(systemData);
        assertEquals(3, systemData.size());
        assertEquals("ProductScreen", systemData.get("screen_name"));
        assertEquals("com.kp.lr.ProductScreen", systemData.get("screen_path"));
        assertEquals("Products", systemData.get("screen_title"));

    }


    public void testSetOrUpdateUsersCustomAttribute() {
        DataHolder.get().setOrUpdateUsersCustomAttributes(userIdentifier, userCustomAttribute);
        Map<String, Object> customAttr = getUserCustomAttribute();
        assertNotNull(customAttr);
        assertEquals(2, customAttr.size());
        assertTrue((boolean) customAttr.get("isMarried"));
        assertEquals(101, customAttr.get("luckyNumber"));
    }


    public void testSetOrUpdateUsersDeviceAttribute() {
        DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, userDeviceAttribute);
        Map<String, Object> deviceData = getDeviceData();
        assertNotNull(deviceData);
        assertEquals(6, deviceData.size());
        assertTrue((boolean) deviceData.get("opt_in_push"));
        assertEquals("Sony", deviceData.get("manufacturer"));
        assertEquals(1l, deviceData.get("session_count"));
        assertEquals(60000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
    }

    public void testSetOrUpdateUserProfileWithoutOperation() {
        DataHolder.get().setOrUpdateUserProfile(userIdentifier, UserSystemAttribute.GENDER.toString(), Gender.MALE.toString(), DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile(userIdentifier, UserDeviceAttribute.COUNTRY.toString(), "India", DataContainer.ANDROID);
        Map<String, Object> userData = getUserSystemData();
        Map<String, Object> deviceData = getDeviceData();
        assertNotNull(userData);
        assertNotNull(deviceData);
        assertEquals(7, deviceData.size());
        assertEquals(6, userData.size());

        assertEquals("shahrukh", userData.get(UserSystemAttribute.FIRST_NAME.toString()));
        assertEquals("8092377032", userData.get(UserSystemAttribute.PHONE.toString()));
        assertEquals(60000l, userData.get(UserSystemAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", userData.get(UserSystemAttribute.CITY.toString()));
        assertEquals("direct", userData.get(UserSystemAttribute.REFERRER.toString()));
        assertEquals("male", userData.get(UserSystemAttribute.GENDER.toString()));


        assertTrue((boolean) deviceData.get(UserDeviceAttribute.OPT_IN_PUSH.toString()));
        assertEquals("Sony", deviceData.get("manufacturer"));
        assertEquals(1l, deviceData.get(UserDeviceAttribute.SESSION_COUNT.toString()));
        assertEquals(60000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
        assertEquals("India", deviceData.get(UserDeviceAttribute.COUNTRY.toString()));
    }

    public void testSetOrUpdateUserProfileWithOpeartion() {
        //test force update  when key is not present
        DataHolder.get().setOrUpdateUserProfile(userIdentifier, UserDeviceAttribute.REGION.toString(), "Maharashtra", DataContainer.ANDROID, Operation.FORCE_UPDATE);
        //test force update  when key is  present
        DataHolder.get().setOrUpdateUserProfile(userIdentifier, UserDeviceAttribute.OPT_IN_PUSH.toString(), false, DataContainer.ANDROID, Operation.FORCE_UPDATE);

        //test opt update when key is not present
        DataHolder.get().setOrUpdateUserProfile(userIdentifier, UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER, Operation.OPT_UPDATE);
        //test opt update when key is  present
        DataHolder.get().setOrUpdateUserProfile(userIdentifier, UserSystemAttribute.FIRST_NAME.toString(), "ravi", DataContainer.USER, Operation.OPT_UPDATE);

        Map<String, Object> userData = getUserSystemData();
        Map<String, Object> deviceData = getDeviceData();
        assertNotNull(userData);
        assertNotNull(deviceData);
        assertEquals(8, deviceData.size());
        assertEquals(7, userData.size());


        assertFalse((boolean) deviceData.get(UserDeviceAttribute.OPT_IN_PUSH.toString()));
        assertEquals("Sony", deviceData.get("manufacturer"));
        assertEquals(1l, deviceData.get(UserDeviceAttribute.SESSION_COUNT.toString()));
        assertEquals(60000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
        assertEquals("India", deviceData.get(UserDeviceAttribute.COUNTRY.toString()));
        assertEquals("Maharashtra", deviceData.get(UserDeviceAttribute.REGION.toString()));


        assertEquals("shahrukh", userData.get(UserSystemAttribute.FIRST_NAME.toString()));
        assertEquals("8092377032", userData.get(UserSystemAttribute.PHONE.toString()));
        assertEquals(60000l, userData.get(UserSystemAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", userData.get(UserSystemAttribute.CITY.toString()));
        assertEquals("direct", userData.get(UserSystemAttribute.REFERRER.toString()));
        assertEquals("male", userData.get(UserSystemAttribute.GENDER.toString()));
        assertEquals("imam", userData.get(UserSystemAttribute.LAST_NAME.toString()));
    }

    public void testIncrementUserProfile() {
        DataHolder.get().incrementUserProfile(userIdentifier, UserDeviceAttribute.TIME_SPENT.toString(), 20000l, DataContainer.ANDROID);
        DataHolder.get().incrementUserProfile(userIdentifier, UserDeviceAttribute.SESSION_COUNT.toString(), 3l, DataContainer.ANDROID);
        DataHolder.get().incrementUserProfile(userIdentifier, UserSystemAttribute.TIME_SPENT.toString(), 10000l, DataContainer.USER);
        Map<String, Object> userData = getUserSystemData();
        Map<String, Object> deviceData = getDeviceData();
        assertNotNull(deviceData);
        assertNotNull(userData);
        assertEquals(8, deviceData.size());
        assertEquals(7, userData.size());


        assertFalse((boolean) deviceData.get(UserDeviceAttribute.OPT_IN_PUSH.toString()));
        assertEquals("Sony", deviceData.get("manufacturer"));
        assertEquals(80000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals(4l, deviceData.get(UserDeviceAttribute.SESSION_COUNT.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
        assertEquals("India", deviceData.get(UserDeviceAttribute.COUNTRY.toString()));
        assertEquals("Maharashtra", deviceData.get(UserDeviceAttribute.REGION.toString()));

        assertEquals("shahrukh", userData.get(UserSystemAttribute.FIRST_NAME.toString()));
        assertEquals("8092377032", userData.get(UserSystemAttribute.PHONE.toString()));
        assertEquals(70000l, userData.get(UserSystemAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", userData.get(UserSystemAttribute.CITY.toString()));
        assertEquals("direct", userData.get(UserSystemAttribute.REFERRER.toString()));
        assertEquals("male", userData.get(UserSystemAttribute.GENDER.toString()));
        assertEquals("imam", userData.get(UserSystemAttribute.LAST_NAME.toString()));

    }


    public void testIncrementUserSystemAttribute() {
        Map<String, Object> systemAttribute = new HashMap<String, Object>();
        systemAttribute.put(UserSystemAttribute.TIME_SPENT.toString(), 10000l);
        systemAttribute.put(UserDeviceAttribute.SESSION_COUNT.toString(), 6l);
        DataHolder.get().incrementUsersSystemAttributes(userIdentifier, systemAttribute);

        Map<String, Object> userData = getUserSystemData();
        Map<String, Object> deviceData = getDeviceData();
        assertNotNull(deviceData);
        assertNotNull(userData);
        assertEquals(8, deviceData.size());
        assertEquals(8, userData.size());


        assertFalse((boolean) deviceData.get(UserDeviceAttribute.OPT_IN_PUSH.toString()));
        assertEquals("Sony", deviceData.get("manufacturer"));
        assertEquals(90000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals(10l, deviceData.get(UserDeviceAttribute.SESSION_COUNT.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
        assertEquals("India", deviceData.get(UserDeviceAttribute.COUNTRY.toString()));
        assertEquals("Maharashtra", deviceData.get(UserDeviceAttribute.REGION.toString()));

        assertEquals("shahrukh", userData.get(UserSystemAttribute.FIRST_NAME.toString()));
        assertEquals("8092377032", userData.get(UserSystemAttribute.PHONE.toString()));
        assertEquals(80000l, userData.get(UserSystemAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", userData.get(UserSystemAttribute.CITY.toString()));
        assertEquals("direct", userData.get(UserSystemAttribute.REFERRER.toString()));
        assertEquals("male", userData.get(UserSystemAttribute.GENDER.toString()));
        assertEquals("imam", userData.get(UserSystemAttribute.LAST_NAME.toString()));
        assertEquals(6l, userData.get(UserSystemAttribute.SESSION_COUNT.toString()));
    }

    public void testIncrementUserCustomAttribute() {
        Map<String, Object> customAttr = new HashMap<String, Object>();
        customAttr.put("luckyNumber", 100);
        DataHolder.get().incrementUsersCustomAttributes(userIdentifier, customAttr);
        Map<String, Object> customUserAttr = getUserCustomAttribute();
        assertNotNull(customUserAttr);
        assertEquals(201, customUserAttr.get("luckyNumber"));
        assertTrue((boolean) customUserAttr.get("isMarried"));
    }

    public void testSetOrUpdateEventAttributes() {
        DataHolder.get().setOrUpdateEventAttributes(EventName.NOTIFICATION_CLICK.toString(), systemEventAttributes);
        DataHolder.get().setOrUpdateEventAttributes("addToCart", customEventAttributes);
        DataHolder.get().setOrUpdateEventAttributes(EventName.WE_WK_SESSION_DELAY.toString(), internalEventAttributes);
        Map<String, Object> eventAttr = getEventAttributes();
        assertNotNull(eventAttr);
        assertEquals(3, eventAttr.size());

        Map<String, Object> notificationClickAttributes = (Map<String, Object>) eventAttr.get(EventName.NOTIFICATION_CLICK.toString());
        assertNotNull(notificationClickAttributes);
        assertEquals(3, notificationClickAttributes.size());
        Map<String, Object> systemAttr = (Map<String, Object>) notificationClickAttributes.get("we_wk_sys");
        assertNotNull(systemAttr);
        assertEquals(5, systemAttr.size());
        assertTrue((boolean) notificationClickAttributes.get("discount"));
        assertEquals("shoes", notificationClickAttributes.get("product"));
        assertEquals("~id1", systemAttr.get("id"));
        assertEquals("~exp1", systemAttr.get("experiment_id"));
        assertEquals("~cta1", systemAttr.get("call_to_action"));
        assertEquals("Sony", systemAttr.get("brand"));
        assertEquals("Mumbai", systemAttr.get("city"));

        Map<String, Object> addToCartAttr = (Map<String, Object>) eventAttr.get("addToCart");
        assertNotNull(addToCartAttr);
        assertEquals(4, addToCartAttr.size());
        systemAttr = (Map<String, Object>) addToCartAttr.get("we_wk_sys");
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals("45001", addToCartAttr.get("product_id"));
        assertEquals("fragrance", addToCartAttr.get("category"));
        assertEquals(12.80, addToCartAttr.get("price"));
        assertEquals("Sony", systemAttr.get("brand"));
        assertEquals("Mumbai", systemAttr.get("city"));

        Map<String, Object> sessionDelayAttr = (Map<String, Object>) eventAttr.get(EventName.WE_WK_SESSION_DELAY.toString());
        assertNotNull(sessionDelayAttr);
        assertEquals(1, sessionDelayAttr.size());
        assertEquals(30001, sessionDelayAttr.get("value"));
    }


    public void testGetDataWithStringAsKey() {
        Object value = DataHolder.get().getData("x");
        assertNotNull(value);
        assertEquals(1, value);

        value = DataHolder.get().getData("y");
        assertNull(value);

        value = DataHolder.get().getData(DataContainer.USER.toString());
        assertNotNull(value);
        assertTrue(value instanceof Map);
        assertEquals(8, ((Map<String, Object>) value).size());
    }

    public void testGetDataWithListAsKey() {
        List<Object> list = new ArrayList<Object>();
        list.add(DataContainer.PAGE.toString());
        list.add(WebEngageConstant.SYSTEM);
        list.add("screen_name");
        Object value = DataHolder.get().getData(list);
        assertNotNull(value);
        assertEquals("ProductScreen", value);

        list.clear();
        list.add(DataContainer.PAGE.toString());
        list.add("x");
        list.add("y");
        value = DataHolder.get().getData(list);
        assertNull(value);
    }

    public void testGetDataForImmutability() {
        Object value = DataHolder.get().getData(DataContainer.USER.toString());
        assertNotNull(value);
        assertTrue(value instanceof Map);
        assertEquals(8, ((Map<String, Object>) value).size());
        Map<String, Object> attr = (Map<String, Object>) value;
        attr.put("x", true);
        attr.put(UserSystemAttribute.FIRST_NAME.toString(), "changed");

        Object newValue = DataHolder.get().getData(DataContainer.USER.toString());
        assertNotNull(newValue);
        assertTrue(newValue instanceof Map);
        assertEquals(8, ((Map<String, Object>) newValue).size());

        assertEquals("shahrukh", ((Map<String, Object>) newValue).get(UserSystemAttribute.FIRST_NAME.toString()));
        assertEquals("8092377032", ((Map<String, Object>) newValue).get(UserSystemAttribute.PHONE.toString()));
        assertEquals(80000l, ((Map<String, Object>) newValue).get(UserSystemAttribute.TIME_SPENT.toString()));
        assertEquals("Mumbai", ((Map<String, Object>) newValue).get(UserSystemAttribute.CITY.toString()));
        assertEquals("direct", ((Map<String, Object>) newValue).get(UserSystemAttribute.REFERRER.toString()));
        assertEquals("male", ((Map<String, Object>) newValue).get(UserSystemAttribute.GENDER.toString()));
        assertEquals("imam", ((Map<String, Object>) newValue).get(UserSystemAttribute.LAST_NAME.toString()));
        assertEquals(6l, ((Map<String, Object>) newValue).get(UserSystemAttribute.SESSION_COUNT.toString()));
        assertFalse(((Map<String, Object>) newValue).containsKey("x"));


        List<Object> list = new ArrayList<Object>();
        list.add(DataContainer.PAGE.toString());
        list.add(WebEngageConstant.SYSTEM);
        Map<String, Object> result = (Map<String, Object>) DataHolder.get().getData(list);
        assertNotNull(result);
        assertEquals(3, result.size());
        result.put("screen_name", "changed");
        result.put("x", 1);


        Map<String, Object> pageSystemData = (Map<String, Object>) DataHolder.get().getData(list);
        assertNotNull(pageSystemData);
        assertEquals(3, pageSystemData.size());
        assertEquals("ProductScreen", pageSystemData.get("screen_name"));
        assertEquals("com.kp.lr.ProductScreen", pageSystemData.get("screen_path"));

    }


    public void beginAllGetMethodsTest() {
        Map<String, Object> deviceAttributes = new HashMap<String, Object>();
        deviceAttributes.put("session_type", "online");
        deviceAttributes.put("sdk_id", BuildConfig.SDK_ID);
        deviceAttributes.put("sdk_version", BuildConfig.FEATURE_VERSION);
        deviceAttributes.put(UserDeviceAttribute.LATITUDE.toString(), 14.2323);
        deviceAttributes.put(UserDeviceAttribute.LONGITUDE.toString(), 72.3434);
        deviceAttributes.put(UserDeviceAttribute.LOCALITY.toString(), "Laxmi Nagar");
        deviceAttributes.put(UserDeviceAttribute.POSTAL_CODE.toString(), "400063");
        deviceAttributes.put("total_page_view_count", 10l);
        deviceAttributes.put("page_view_count_session", 5l);
        deviceAttributes.put("b_session_count", 15l);
        deviceAttributes.put(UserDeviceAttribute.LAST_SEEN.toString(), new Date(1461049875000l));
        deviceAttributes.put(UserDeviceAttribute.LAST_LOGGED_IN.toString(), new Date(14610497875000l));

        DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, deviceAttributes);


        DataHolder.get().setAppForeground(true);

        Map<String, Object> runtimeConfig = new HashMap<String, Object>();
        runtimeConfig.put("tzo", 19800l);
        Map<String, Object> events = new HashMap<String, Object>();
        List<Object> pageDelayValues = new ArrayList<Object>();
        pageDelayValues.add(30000l);
        pageDelayValues.add(40000l);
        events.put(EventName.WE_WK_PAGE_DELAY.toString(), pageDelayValues);
        List<Object> sessionDelayValues = new ArrayList<Object>();
        sessionDelayValues.add(10000l);
        sessionDelayValues.add(20000l);
        events.put(EventName.WE_WK_SESSION_DELAY.toString(), sessionDelayValues);
        runtimeConfig.put("events", events);
        runtimeConfig.put("gbp", "http://c.webengage.com");
        DataHolder.get().setData(WebEngageConstant.CONFIG, runtimeConfig);


        Map<String, Object> scopes = new HashMap<String, Object>();
        scopes.put("not-1_view", 2l);
        scopes.put("not-1[sc1]_view", 1l);
        scopes.put("not-1_view_session", 1l);
        scopes.put("not-1[sc1]_close_session", 1l);
        scopes.put("not-1[sc1]_close", 1l);
        scopes.put("not-1[sc1]_hide", 1l);
        scopes.put("not-1[sc1]_hide_session", 1l);
        scopes.put("not-1[sc1]_click", 1l);
        DataHolder.get().setData(DataContainer.SCOPES.toString(), scopes);

        Map<String, Object> journey = new HashMap<String, Object>();
        Map<String, Object> j1 = new HashMap<String, Object>();
        j1.put("id", "sc1");
        journey.put("j1", j1);
        DataHolder.get().setData(DataContainer.JOURNEY.toString(), journey);


    }

    public void testGetLatestSessionType() {
        Object value = DataHolder.get().getLatestSessionType();
        assertNotNull(value);
        assertEquals("online", value);
    }

    public void testGetSDKId() {
        Object value = DataHolder.get().getSDKId();
        assertNotNull(value);
        assertEquals(2, value);
    }

    public void testGetSDKVersion() {
        Object value = DataHolder.get().getSDKVersion();
        assertNotNull(value);
        assertEquals(BuildConfig.FEATURE_VERSION, value);
    }

    public void testGetLatitude() {
        Object value = DataHolder.get().getLatitude();
        assertNotNull(value);
        assertEquals(14.2323, value);
    }

    public void testGetLongitude() {
        Object value = DataHolder.get().getLongitude();
        assertNotNull(value);
        assertEquals(72.3434, value);
    }

    public void testGetCity() {
        Object value = DataHolder.get().getCity();
        assertNotNull(value);
        assertEquals("Mumbai", value);
    }

    public void testGetCountry() {
        Object value = DataHolder.get().getCountry();
        assertNotNull(value);
        assertEquals("India", value);
    }

    public void testGetRegion() {
        Object value = DataHolder.get().getRegion();
        assertNotNull(value);
        assertEquals("Maharashtra", value);
    }

    public void testGetLocality() {
        Object value = DataHolder.get().getLocality();
        assertNotNull(value);
        assertEquals("Laxmi Nagar", value);
    }

    public void testGetPostalCode() {
        Object value = DataHolder.get().getPostalCode();
        assertNotNull(value);
        assertEquals("400063", value);
    }

    public void testGetTotalPageViewCount() {
        Object value = DataHolder.get().getTotalPageViewCount();
        assertNotNull(value);
        assertEquals(10l, value);
    }

    public void testGetSessionPageViewCount() {
        Object value = DataHolder.get().getSessionPageViewCount();
        assertNotNull(value);
        assertEquals(5l, value);
    }

    public void testGetForegroundSessionCount() {
        Object value = DataHolder.get().getForegroundSessionCount();
        assertNotNull(value);
        assertEquals(10l, value);
    }

    public void testGetbackgroundSessionCount() {
        Object value = DataHolder.get().getBackgroundSessionCount();
        assertNotNull(value);
        assertEquals(15l, value);
    }

    public void testGetScreenPath() {
        Object value = DataHolder.get().getScreenPath();
        assertNotNull(value);
        assertEquals("com.kp.lr.ProductScreen", value);
    }

    public void testGetScreenTitle() {
        Object value = DataHolder.get().getScreenTitle();
        assertNotNull(value);
        assertEquals("Products", value);
    }

    public void testGetScreenName() {
        Object value = DataHolder.get().getScreenName();
        assertNotNull(value);
        assertEquals("ProductScreen", value);
    }


    public void testGetEntityTotalViewCountPerScope() {
        Map<String, Object> entityObj = new HashMap<String, Object>();
        entityObj.put("journeyId", "j1");
        entityObj.put("notificationEncId", "not-1");
        Long value = DataHolder.get().getEntityTotalViewCountPerScope(entityObj, WebEngageConstant.Entity.NOTIFICATION);
        assertEquals(1l, value.longValue());
    }

    public void testGetEntityTotalViewCountAcrossScope() {
        assertEquals(2l, DataHolder.get().getEntityTotalViewCountAcrossScopes("not-1").longValue());
    }

    public void testGetEntityTotalViewCountInSessionAcrossScopes() {
        assertEquals(1l, DataHolder.get().getEntityTotalViewCountInSessionAcrossScopes("not-1").longValue());
    }

    public void testGetEntityTotalCloseCountInSessionPerScope() {
        Map<String, Object> entityObj = new HashMap<String, Object>();
        entityObj.put("journeyId", "j1");
        entityObj.put("notificationEncId", "not-1");
        assertEquals(1l, DataHolder.get().getEntityTotalCloseCountInSessionPerScope(entityObj, WebEngageConstant.Entity.NOTIFICATION).longValue());
    }

    public void testGetEntityTotalCloseCountPerScope() {
        Map<String, Object> entityObj = new HashMap<String, Object>();
        entityObj.put("journeyId", "j1");
        entityObj.put("notificationEncId", "not-1");
        assertEquals(1l, DataHolder.get().getEntityTotalCloseCountPerScope(entityObj, WebEngageConstant.Entity.NOTIFICATION).longValue());
    }

    public void testGetEntityTotalHideCountInSessionPerScope() {
        Map<String, Object> entityObj = new HashMap<String, Object>();
        entityObj.put("journeyId", "j1");
        entityObj.put("notificationEncId", "not-1");
        assertEquals(1l, DataHolder.get().getEntityTotalHideCountInSessionPerScope(entityObj, WebEngageConstant.Entity.NOTIFICATION).longValue());
    }


    public void testGetEntityTotalHideCountPerScope() {
        Map<String, Object> entityObj = new HashMap<String, Object>();
        entityObj.put("journeyId", "j1");
        entityObj.put("notificationEncId", "not-1");
        assertEquals(1l, DataHolder.get().getEntityTotalHideCountPerScope(entityObj, WebEngageConstant.Entity.NOTIFICATION).longValue());
    }

    public void testGetEntityTotalClickCountPerScope() {
        Map<String, Object> entityObj = new HashMap<String, Object>();
        entityObj.put("journeyId", "j1");
        entityObj.put("notificationEncId", "not-1");
        assertEquals(1l, DataHolder.get().getEntityTotalClickCountPerScope(entityObj, WebEngageConstant.Entity.NOTIFICATION).longValue());
    }


    public void testGetCustomScreenData() {
        Map<String, Object> value = DataHolder.get().getCustomScreenData();
        assertNotNull(value);
        assertEquals(2, value.size());
        assertEquals("shoes", value.get("product"));
        assertTrue((boolean) value.get("discount"));

    }


    public void testGetSystemScreenData() {
        Map<String, Object> systemData = DataHolder.get().getSystemScreenData();
        assertNotNull(systemData);
        assertEquals(3, systemData.size());
        assertEquals("ProductScreen", systemData.get("screen_name"));
        assertEquals("com.kp.lr.ProductScreen", systemData.get("screen_path"));
        assertEquals("Products", systemData.get("screen_title"));
    }


    public void testGetUserLastSeen() {
        Date value = DataHolder.get().getUserLastSeen();
        assertNotNull(value);
        assertEquals(1461049875000l, value.getTime());

    }


    public void testGetEventCriteria() {
        Map<String, Object> value = DataHolder.get().getEventCriteria("id1");
        assertNotNull(value);
        assertEquals(3, value.size());
        assertEquals("id1", value.get("criteria_id"));
        assertEquals("SUM", value.get("function"));
        assertEquals(100, value.get("val"));
    }


    public void testGetDeviceData() {
        Object value = DataHolder.get().getDeviceData("city");
        assertEquals("Mumbai", value);
    }


    public void testGetUserSystemData() {
        Object value = DataHolder.get().getUserSystemData(UserSystemAttribute.FIRST_NAME.toString());
        assertEquals("shahrukh", value);
    }

    public void testGetUserLastLoggedIn() {
        Date value = DataHolder.get().getUserLastLoggedIn();
        assertNotNull(value);
        assertEquals(14610497875000l, value.getTime());
    }


    public void testIsAppForeground() {
        Boolean value = DataHolder.get().isAppForeground();
        assertNotNull(value);
        assertTrue(value);
    }

    public void testGetTZO() {
        Long value = DataHolder.get().getTZO();
        assertEquals(19800l, value.longValue());
    }

    public void testGetPageDelayValues() {
        List<Object> values = DataHolder.get().getPageDelayValues();
        assertNotNull(values);
        assertEquals(30000l, values.get(0));
        assertEquals(40000l, values.get(1));
    }

    public void testGetSessionDelayValues() {
        List<Object> values = DataHolder.get().getSessionDelayValues();
        assertNotNull(values);
        assertEquals(10000l, values.get(0));
        assertEquals(20000l, values.get(1));
    }

    public void testGetBaseUrl() {
        String value = DataHolder.get().getBaseUrl();
        assertEquals("http://c.webengage.com", value);
    }

    public void testGetOptInValue() {
        Map<String, Object> deviceOptIn = new HashMap<String, Object>();
        deviceOptIn.put(UserDeviceAttribute.OPT_IN_PUSH, null);
        deviceOptIn.put(UserDeviceAttribute.OPT_IN_INAPP, null);

        Map<String, Object> userOptIn = new HashMap<String, Object>();
        userOptIn.put(UserSystemAttribute.PUSH_OPT_IN.toString(), null);
        DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, deviceOptIn);
        DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, userOptIn);


        assertTrue(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.PUSH));// null & null
        assertTrue(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.NOTIFICATION));


        deviceOptIn.put(UserDeviceAttribute.OPT_IN_PUSH, true);
        deviceOptIn.put(UserDeviceAttribute.OPT_IN_INAPP, true);
        DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, deviceOptIn);

        assertTrue(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.PUSH));//true && null
        assertTrue(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.NOTIFICATION));

        deviceOptIn.put(UserDeviceAttribute.OPT_IN_PUSH, false);
        deviceOptIn.put(UserDeviceAttribute.OPT_IN_INAPP, false);
        DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, deviceOptIn);

        assertFalse(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.PUSH));//false && null
        assertFalse(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.NOTIFICATION));

        deviceOptIn.put(UserDeviceAttribute.OPT_IN_PUSH, null);
        DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, deviceOptIn);

        userOptIn.put(UserSystemAttribute.PUSH_OPT_IN.toString(), true);
        DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, userOptIn);

        assertTrue(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.PUSH));//null && true


        userOptIn.put(UserSystemAttribute.PUSH_OPT_IN.toString(), false);
        DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, userOptIn);

        assertFalse(DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.PUSH));//null && false


    }

    public void testClearSessionDeviceData() {
        DataHolder.get().clearSessionLevelData(userIdentifier, DataContainer.ANDROID);
        Map<String, Object> deviceData = getDeviceData();
        assertEquals(21, deviceData.size());

        assertNull(deviceData.get(UserDeviceAttribute.OPT_IN_PUSH.toString()));
        assertEquals("Sony", deviceData.get("manufacturer"));
        assertEquals(90000l, deviceData.get(UserDeviceAttribute.TIME_SPENT.toString()));
        assertEquals(10l, deviceData.get(UserDeviceAttribute.SESSION_COUNT.toString()));
        assertEquals("Mumbai", deviceData.get(UserDeviceAttribute.CITY.toString()));
        assertEquals("direct", deviceData.get(UserDeviceAttribute.REFERRER.toString()));
        assertEquals("India", deviceData.get(UserDeviceAttribute.COUNTRY.toString()));
        assertEquals("Maharashtra", deviceData.get(UserDeviceAttribute.REGION.toString()));

        assertEquals("online", deviceData.get("session_type"));
        assertEquals(2, deviceData.get("sdk_id"));
        assertEquals(BuildConfig.FEATURE_VERSION, deviceData.get("sdk_version"));
        assertEquals(14.2323, deviceData.get(UserDeviceAttribute.LATITUDE.toString()));
        assertEquals(72.3434, deviceData.get(UserDeviceAttribute.LONGITUDE.toString()));
        assertEquals("400063", deviceData.get(UserDeviceAttribute.POSTAL_CODE.toString()));
        assertEquals("Laxmi Nagar", deviceData.get(UserDeviceAttribute.LOCALITY.toString()));
        assertEquals(10l, deviceData.get("total_page_view_count"));
        assertNull(deviceData.get("page_view_count_session"));
        assertEquals(15l, deviceData.get("b_session_count"));
        assertEquals(1461049875000l, ((Date) deviceData.get(UserDeviceAttribute.LAST_SEEN.toString())).getTime());
        assertEquals(14610497875000l, ((Date) deviceData.get(UserDeviceAttribute.LAST_LOGGED_IN.toString())).getTime());

    }

    public void testClearScreenEvents() {
        Map<String, Object> events = (Map<String, Object>) DataHolder.get().getData(DataContainer.EVENT.toString());
        assertNotNull(events);
        assertEquals(3, events.size());

        DataHolder.get().clearScreenEvents();

        events = (Map<String, Object>) DataHolder.get().getData(DataContainer.EVENT.toString());
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(EventName.WE_WK_SESSION_DELAY.toString(), events.keySet().iterator().next());

    }

    public void testClearAllEvents() {
        DataHolder.get().clearAllEvents();
        Map<String, Object> events = (Map<String, Object>) DataHolder.get().getData(DataContainer.EVENT.toString());
        assertNull(events);
    }

    public void testClearScreenData() {
        DataHolder.get().clearScreenData();
        assertNull(getPageData());
        assertNull(DataHolder.get().getSystemScreenData());
        assertNull(DataHolder.get().getCustomScreenData());
    }

    public void testForListIndex() {
        DataHolder.get().container.clear();


        List<Object> luckyNumbers = new ArrayList<Object>();
        luckyNumbers.add(100);
        luckyNumbers.add(101);

        DataHolder.get().setOrUpdateUserProfile("userId", "luckyNumbers", luckyNumbers, DataContainer.ATTR);
        List<Object> path = new ArrayList<Object>();
        path.add(DataContainer.ATTR.toString());
        path.add("luckyNumbers");
        path.add(5);

        DataHolder.get().setData(path, 200);

        Map<String, Object> result = getUserCustomAttribute();
        List<Object> lucky = (List<Object>) result.get("luckyNumbers");
        assertEquals(6, lucky.size());
        assertEquals(100, lucky.get(0));
        assertEquals(101, lucky.get(1));
        assertEquals(null, lucky.get(2));
        assertEquals(null, lucky.get(3));
        assertEquals(null, lucky.get(4));
        assertEquals(200, lucky.get(5));

        path = new ArrayList<Object>();
        path.add("a");
        path.add(3);
        path.add("b");
        path.add("c");
        path.add(5);
        DataHolder.get().setData(path, "hello");


        List<Object> a = (List<Object>) DataHolder.get().container.get("a");
        assertEquals(4, a.size());
        assertEquals(null, a.get(0));
        assertEquals(null, a.get(1));
        assertEquals(null, a.get(2));

        Map<String, Object> mapB = (Map<String, Object>) a.get(3);
        assertEquals(1, mapB.size());
        Map<String, Object> mapC = (Map<String, Object>) mapB.get("b");
        assertEquals(1, mapC.size());

        List<Object> listC = (List<Object>) mapC.get("c");
        assertEquals(6, listC.size());
        assertEquals(null, listC.get(0));
        assertEquals(null, listC.get(1));
        assertEquals(null, listC.get(2));
        assertEquals(null, listC.get(3));
        assertEquals(null, listC.get(4));
        assertEquals("hello", listC.get(5));


    }


    public Map<String, Object> getUserSystemData() {
        return (Map<String, Object>) DataHolder.get().container.get(DataContainer.USER.toString());
    }

    public Map<String, Object> getDeviceData() {
        return (Map<String, Object>) DataHolder.get().container.get(DataContainer.ANDROID.toString());
    }

    public Map<String, Object> getPageData() {
        return (Map<String, Object>) DataHolder.get().container.get(DataContainer.PAGE.toString());
    }

    public Map<String, Object> getEventCriterias() {
        return (Map<String, Object>) DataHolder.get().container.get(DataContainer.EVENT_CRITERIA.toString());
    }

    public Map<String, Object> getUserCustomAttribute() {
        return (Map<String, Object>) DataHolder.get().container.get(DataContainer.ATTR.toString());
    }

    public Map<String, Object> getEventAttributes() {
        return (Map<String, Object>) DataHolder.get().container.get(DataContainer.EVENT.toString());
    }
}
