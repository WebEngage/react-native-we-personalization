package com.webengage.sdk.android.actions.database;

import com.webengage.sdk.android.UserSystemAttribute;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by shahrukhimam on 11/06/16.
 */

public class GetResolvedDataTest {

    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void testSequentially() {
        testForUser();
        testForEvents();
        testForScreen();
    }

    private void testForUser() {
        List<Object> list = new ArrayList<Object>();
        list.add("user");
        testWithOnlyUser(list);

        list.add("system");
        testWithUserAndSystem(list);

        list.clear();
        list.add("user");
        list.add("custom");
        testWithUserAndCustom(list);

        list.clear();
        list.add("user");
        list.add("system");
        list.add(UserSystemAttribute.FIRST_NAME.toString());
        testWithUserSystemAndAttrName(list);

        list.clear();
        list.add("user");
        list.add("custom");
        list.add("age");
        testWithUserCustomAndAttrName(list);


        list.clear();
        list.add("user");
        list.add("custom");
        list.add("orderId");
        list.add("1");
        testUserCustomAndAttrNameWithIndex(list);


    }

    private void testWithOnlyUser(List<Object> path) {
        //test when  values of system and custom attr is null;
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when values of system and custom attr is empty map
        DataHolder.get().setData(DataContainer.USER.toString(), new HashMap<String, Object>());
        DataHolder.get().setData(DataContainer.ATTR.toString(), new HashMap<String, Object>());
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system attr is empty and custom attr is non-empty
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> systemAttr = (Map<String, Object>) result.get("system");
        Map<String, Object> customAttr = (Map<String, Object>) result.get("custom");
        assertNull(systemAttr);
        assertNotNull(customAttr);
        assertEquals(2, customAttr.size());
        assertEquals(13, customAttr.get("age"));
        assertEquals("abc101", customAttr.get("orderId"));


        //test when custom attr is null and system attr is non-empty
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);

        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        systemAttr = (Map<String, Object>) result.get("system");
        customAttr = (Map<String, Object>) result.get("custom");
        assertNull(customAttr);
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals("shahrukh", systemAttr.get("first_name"));
        assertEquals("imam", systemAttr.get("last_name"));


        //test when custom and system attr are non-empty
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        systemAttr = (Map<String, Object>) result.get("system");
        customAttr = (Map<String, Object>) result.get("custom");
        assertNotNull(customAttr);
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals("shahrukh", systemAttr.get("first_name"));
        assertEquals("imam", systemAttr.get("last_name"));
        assertEquals(2, customAttr.size());
        assertEquals(13, customAttr.get("age"));
        assertEquals("abc101", customAttr.get("orderId"));

    }


    private void testWithUserAndSystem(List<Object> path) {
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);

        //test when values of system and custom attr is null
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);


        // test when values of system and custom attr is empty map
        DataHolder.get().setData(DataContainer.USER.toString(), new HashMap<String, Object>());
        DataHolder.get().setData(DataContainer.ATTR.toString(), new HashMap<String, Object>());
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system attr is empty and custom attr is non-empty
        DataHolder.get().setData(DataContainer.USER.toString(), new HashMap<String, Object>());
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is null and system attr is non-empty
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("shahrukh", result.get("first_name"));
        assertEquals("imam", result.get("last_name"));

        //test when custom and system attr are non-empty
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("shahrukh", result.get("first_name"));
        assertEquals("imam", result.get("last_name"));

    }

    public void testWithUserAndCustom(List<Object> path) {
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);

        //test when values of system and custom attr is null
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);


        // test when values of system and custom attr is empty map
        DataHolder.get().setData(DataContainer.USER.toString(), new HashMap<String, Object>());
        DataHolder.get().setData(DataContainer.ATTR.toString(), new HashMap<String, Object>());
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system attr is empty and custom attr is non-empty
        DataHolder.get().setData(DataContainer.USER.toString(), new HashMap<String, Object>());
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(13, result.get("age"));
        assertEquals("abc101", result.get("orderId"));

        //test when custom attr is null and system attr is non-empty
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom and system attr are non-empty
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(13, result.get("age"));
        assertEquals("abc101", result.get("orderId"));
    }

    public void testWithUserSystemAndAttrName(List<Object> path) {
        //test when attribute value is present
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals("shahrukh", result.toString());

        //test when attribute value is not present
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when attribute value is list
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        List<String> name = new ArrayList<>();
        name.add("shahrukh");
        name.add("nitin");
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), name, DataContainer.USER);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        List<Object> names = (List<Object>) result;
        assertNotNull(names);
        assertEquals(2, names.size());
        assertEquals("shahrukh", names.get(0));
        assertEquals("nitin", names.get(1));


    }

    public void testWithUserCustomAndAttrName(List<Object> path) {
        //test when attribute value is present
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.FIRST_NAME.toString(), "shahrukh", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", 13, DataContainer.ATTR);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(13, result);

        //test when attribute value is not present
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        DataHolder.get().setOrUpdateUserProfile("abc", UserSystemAttribute.LAST_NAME.toString(), "imam", DataContainer.USER);
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", "abc101", DataContainer.ATTR);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when attribute value is list
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        List<Integer> age = new ArrayList<>();
        age.add(19);
        age.add(20);
        DataHolder.get().setOrUpdateUserProfile("abc", "age", age, DataContainer.ATTR);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        List<Object> ages = (List<Object>) result;
        assertNotNull(ages);
        assertEquals(2, ages.size());
        assertEquals(19, ages.get(0));
        assertEquals(20, ages.get(1));
    }

    public void testUserCustomAndAttrNameWithIndex(List<Object> path) {
        //test when index is string
        DataHolder.get().setData(DataContainer.ATTR.toString(), null);
        DataHolder.get().setData(DataContainer.USER.toString(), null);
        List<String> orderId = new ArrayList<String>();
        orderId.add("~1");
        orderId.add("~2");
        orderId.add("~3");
        DataHolder.get().setOrUpdateUserProfile("abc", "orderId", orderId, DataContainer.ATTR);
        String result = (String) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("~2", result);

        //test when index is integer
        path.set(3, 1);
        result = (String) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("~2", result);
    }


    private void testForEvents() {
        List<Object> path = new ArrayList<Object>();
        path.add("event");
        path.add("custom");
        path.add("purchase");
        testForCustomEventNameOnly(path);

        path.clear();
        path.add("event");
        path.add("system");
        path.add("notification_view");
        testForSystemEventNameOnly(path);

        path.clear();
        path.add("event");
        path.add("custom");
        path.add("purchase");
        path.add("custom");
        testForCustomEventNameWithAllCustomAttr(path);

        path.clear();
        path.add("event");
        path.add("custom");
        path.add("purchase");
        path.add("system");
        testForCustomEventNameWithAllSystemAttr(path);


        path.clear();
        path.add("event");
        path.add("system");
        path.add("notification_view");
        path.add("custom");
        testForSystemEventNameWithAllCustomAttr(path);

        path.clear();
        path.add("event");
        path.add("custom");
        path.add("purchase");
        path.add("custom");
        path.add("price");
        testForCustomEventWithSingleCustomAttribute(path);

        path.clear();
        path.add("event");
        path.add("custom");
        path.add("purchase");
        path.add("system");
        path.add("model");
        testForCustomEventWithSingleSystemAttribute(path);

        path.clear();
        path.add("event");
        path.add("system");
        path.add("notification_view");
        path.add("custom");
        path.add("price");
        testForSystemEventWithSingleCustomAttr(path);

        path.clear();
        path.add("event");
        path.add("custom");
        path.add("purchase");
        path.add("custom");
        path.add("price");
        path.add("1");
        testForCustomEventWithIndexOfAttribute(path);

    }


    public void testForCustomEventNameOnly(List<Object> path) {
        //test when custom and system attr is null or empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<>();
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty or null and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> systemAttr = (Map<String, Object>) result.get("system");
        Map<String, Object> customAttr = (Map<String, Object>) result.get("custom");
        assertNull(customAttr);
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals(1461049875000l, ((Date) systemAttr.get("event_time")).getTime());
        assertEquals("sony", systemAttr.get("model"));

        //test when custom attr is non empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        systemAttr = (Map<String, Object>) result.get("system");
        customAttr = (Map<String, Object>) result.get("custom");
        assertNotNull(customAttr);
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals(1461049875000l, ((Date) systemAttr.get("event_time")).getTime());
        assertEquals("sony", systemAttr.get("model"));
        assertEquals(2, customAttr.size());
        assertEquals(100, customAttr.get("price"));
        assertEquals(true, customAttr.get("discount"));


        //test when custom in non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        systemAttr = (Map<String, Object>) result.get("system");
        customAttr = (Map<String, Object>) result.get("custom");
        assertNotNull(customAttr);
        assertNull(systemAttr);
        assertEquals(2, customAttr.size());
        assertEquals(100, customAttr.get("price"));
        assertEquals(true, customAttr.get("discount"));


    }


    public void testForSystemEventNameOnly(List<Object> path) {
        //test when custom and system attr is null or empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<>();
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty or null and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> systemAttr = (Map<String, Object>) result.get("system");
        Map<String, Object> customAttr = (Map<String, Object>) result.get("custom");
        assertNull(customAttr);
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals(1461049875000l, ((Date) systemAttr.get("event_time")).getTime());
        assertEquals("sony", systemAttr.get("model"));

        //test when custom attr is non empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        systemAttr = (Map<String, Object>) result.get("system");
        customAttr = (Map<String, Object>) result.get("custom");
        assertNotNull(customAttr);
        assertNotNull(systemAttr);
        assertEquals(2, systemAttr.size());
        assertEquals(1461049875000l, ((Date) systemAttr.get("event_time")).getTime());
        assertEquals("sony", systemAttr.get("model"));
        assertEquals(2, customAttr.size());
        assertEquals(100, customAttr.get("price"));
        assertEquals(true, customAttr.get("discount"));


        //test when custom in non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        systemAttr = (Map<String, Object>) result.get("system");
        customAttr = (Map<String, Object>) result.get("custom");
        assertNotNull(customAttr);
        assertNull(systemAttr);
        assertEquals(2, customAttr.size());
        assertEquals(100, customAttr.get("price"));
        assertEquals(true, customAttr.get("discount"));
    }

    public void testForCustomEventNameWithAllCustomAttr(List<Object> path) {
        //test when custom and system attr is null
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", null);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get("price"));
        assertEquals(true, result.get("discount"));

        //test when custom attr is non-empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get("price"));
        assertEquals(true, result.get("discount"));
    }

    public void testForCustomEventNameWithAllSystemAttr(List<Object> path) {
        //test when custom and system attr is null
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", null);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1461049875000l, ((Date) result.get("event_time")).getTime());
        assertEquals("sony", result.get("model"));

        //test when custom attr is non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is non-empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("sony", result.get("model"));
        assertEquals(1461049875000l, ((Date) result.get("event_time")).getTime());
    }


    public void testForSystemEventNameWithAllCustomAttr(List<Object> path) {
        //test when custom and system attr is null or empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<>();
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty or null and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is non empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get("price"));
        assertEquals(true, result.get("discount"));


        //test when custom in non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get("price"));
        assertEquals(true, result.get("discount"));
    }


    public void testForCustomEventWithSingleCustomAttribute(List<Object> path) {
        //test when custom and system attr is null
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", null);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(100, result);

        //test when custom attr is non-empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(100, result);
    }


    public void testForCustomEventWithSingleSystemAttribute(List<Object> path) {
        //test when custom and system attr is null
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", null);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("sony", result);

        //test when custom attr is non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is non-empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("sony", result);
    }


    public void testForSystemEventWithSingleCustomAttr(List<Object> path) {
        //test when custom and system attr is null or empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<>();
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is empty or null and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        Map<String, Object> systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when custom attr is non empty and system attr is non-empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        systemAttributes = new HashMap<>();
        systemAttributes.put("event_time", new Date(1461049875000l));
        systemAttributes.put("model", "sony");
        attributes.put("we_wk_sys", systemAttributes);
        attributes.put("price", 100);
        attributes.put("discount", true);
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(100, result);


        //test when custom in non-empty and system attr is empty
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        attributes = new HashMap<String, Object>();
        attributes.put("price", 100);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("we_notification_view", attributes);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(100, result);
    }


    public void testForCustomEventWithIndexOfAttribute(List<Object> path) {
        //test when index is string
        List<Object> price = new ArrayList<Object>();
        price.add(100);
        price.add(200);
        price.add(300);
        DataHolder.get().setData(DataContainer.LATEST_EVENT.toString(), null);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("price", price);
        attributes.put("discount", true);
        attributes.put("we_wk_sys", new HashMap<String, Object>());
        DataHolder.get().setOrUpdateLatestEventCache("purchase", attributes);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(200, result);


        //test when index is integer
        path.set(5, 2);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(300, result);
    }

    private void testForScreen() {
        List<Object> path = new ArrayList<Object>();
        path.add("screen");

        testForScreenOnly(path);

        path.clear();
        path.add("screen");
        path.add("system");
        testForScreenAndSystem(path);

        path.clear();
        path.add("screen");
        path.add("custom");
        testForScreenAndCustom(path);

        path.clear();
        path.add("screen");
        path.add("system");
        path.add("screen_name");
        testForScreenSystemAndAttrName(path);

        path.clear();
        path.add("screen");
        path.add("custom");
        path.add("brand");
        testForScreenCustomAndAttrName(path);
    }

    private void testForScreenOnly(List<Object> path) {
        //test when system and custom are null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system is null and custom is non-null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> custom = (Map<String, Object>) result.get("custom");
        assertNotNull(custom);
        assertEquals("puma", custom.get("brand"));
        assertEquals(3, custom.get("cart_items"));


        //test when system is not null and custom is non null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        custom = (Map<String, Object>) result.get("custom");
        Map<String, Object> system = (Map<String, Object>) result.get("system");
        assertNotNull(custom);
        assertEquals("puma", custom.get("brand"));
        assertEquals(3, custom.get("cart_items"));
        assertNotNull(system);
        assertEquals("productScreen", system.get("screen_name"));
        assertEquals("pp", system.get("screen_title"));


        //test when system is not null and custom is null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(1, result.size());
        custom = (Map<String, Object>) result.get("custom");
        system = (Map<String, Object>) result.get("system");
        assertNull(custom);
        assertNotNull(system);
        assertEquals("productScreen", system.get("screen_name"));
        assertEquals("pp", system.get("screen_title"));


    }


    public void testForScreenAndSystem(List<Object> path) {
        //test when system and custom are null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system is null and custom is non-null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);


        //test when system is not null and custom is non null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("productScreen", result.get("screen_name"));
        assertEquals("pp", result.get("screen_title"));


        //test when system is not null and custom is null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("productScreen", result.get("screen_name"));
        assertEquals("pp", result.get("screen_title"));
    }


    public void testForScreenAndCustom(List<Object> path) {
        //test when system and custom are null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system is null and custom is non-null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals(2, result.size());
        assertEquals("puma", result.get("brand"));
        assertEquals(3, result.get("cart_items"));


        //test when system is not null and custom is non null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("puma", result.get("brand"));
        assertEquals(3, result.get("cart_items"));


        //test when system is not null and custom is null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = (Map<String, Object>) RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);
    }


    public void testForScreenSystemAndAttrName(List<Object> path) {
        //test when system and custom are null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system is null and custom is non-null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);


        //test when system is not null and custom is non null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("productScreen", result);


        //test when system is not null and custom is null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("productScreen", result);
    }


    private void testForScreenCustomAndAttrName(List<Object> path){
        //test when system and custom are null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
       Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);

        //test when system is null and custom is non-null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("puma",result);


        //test when system is not null and custom is non null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        customData = new HashMap<String, Object>();
        customData.put("brand", "puma");
        customData.put("cart_items", 3);
        DataHolder.get().setCustomScreenData(customData);
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertEquals("puma",result);


        //test when system is not null and custom is null
        DataHolder.get().setData(DataContainer.PAGE.toString(), null);
        systemData = new HashMap<String, Object>();
        systemData.put("screen_name", "productScreen");
        systemData.put("screen_title", "pp");
        DataHolder.get().setSystemScreenData(systemData);
        result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(path);
        assertNull(result);
    }

}
