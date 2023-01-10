package com.webengage.sdk.android.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class DataTypeTest {
    private static HashMap<String, Object> inputHashMap, outputHashMap, expectedHashMap, innerMap;
    private static List<Object> inputList, outputList, expectedList, innerList;
    private static final int DO_NOT_FILTER = 0;
    private static final int FILTER_TO_NULL = 1;
    private static final int FILTER_TO_STRING = 2;
    private static final int FILTER_TO_DISCARD = 3;
    private static final int FILTER = FILTER_TO_STRING;

    @BeforeClass
    public static void setUp() {
        // Nothing to setup
    }

    @Test
    public void testMethods() {
        // Clone internal
        testCloneInternalList();
        testCloneInternalMap();
        testCloneInternalMapInMap();
        testCloneInternalMapInList();

        // Clone external
        testCloneExternalList();
        testCloneExternalMap();
        testCloneExternalMapInMap();
        testCloneExternalMapInList();
    }

    // Clone internal
    public void testCloneInternalList() {
        inputList = new ArrayList<>();
        inputList.add("product-1");
        inputList.add(new StringBuffer("product-2"));
        inputList.add(new StringBuilder("product-3"));
        inputList.add(new BigDecimal("75.25"));

        try {
            outputList = (ArrayList<Object>) DataType.cloneInternal(inputList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneInternalList", String.valueOf(inputList), String.valueOf(outputList));
    }

    public void testCloneInternalMap() {
        inputHashMap = new HashMap<>();
        inputHashMap.put("key1", "value1");
        inputHashMap.put("key2", "value2");
        inputHashMap.put("key3", new StringBuilder("value3"));
        inputHashMap.put("key4", new Integer(100));

        try {
            outputHashMap = (HashMap<String, Object>) DataType.cloneInternal(inputHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneInternalMap", String.valueOf(inputHashMap), String.valueOf(outputHashMap));
    }

    public void testCloneInternalMapInList() {
        // Input
        inputList = new ArrayList<>();

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1234");
        innerMap.put("product-name", "coffee-cup");
        innerMap.put("quantity", 1);
        inputList.add(innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1235");
        innerMap.put("product-name", "pen-drive");
        innerMap.put("quantity", 2);
        inputList.add(innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1236");
        innerMap.put("product-name", "wrist-watch");
        innerMap.put("quantity", 1);
        innerMap.put("discount-applied", false);
        inputList.add(innerMap);

        try {
            outputList = (List<Object>) DataType.cloneInternal(inputList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneInternalMapInList", inputList, outputList);
    }

    public void testCloneInternalMapInMap() {
        // Input
        inputHashMap = new HashMap<>();

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1234");
        innerMap.put("product-name", "coffee-cup");
        innerMap.put("product-price", 25);
        inputHashMap.put("product-1", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1235");
        innerMap.put("product-name", "pen-drive");
        innerMap.put("product-price", 50.00);
        inputHashMap.put("product-2", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1236");
        innerMap.put("product-name", "laptop-bag");
        innerMap.put("product-price", new BigDecimal("75.00"));
        inputHashMap.put("product-3", innerMap);

        try {
            outputHashMap = (HashMap<String, Object>) DataType.cloneInternal(inputHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneInternalMapInMap", String.valueOf(inputHashMap), String.valueOf(outputHashMap));
    }

    // Clone external
    public void testCloneExternalList() {
        // Input
        inputList = new ArrayList<>();
        inputList.add("product-1");
        inputList.add(new StringBuffer("product-2"));
        inputList.add(new StringBuilder("product-3"));
        innerMap.clear();
        innerMap.put("device-name", "Redmi 1s");
        innerMap.put("device-os", new StringBuilder("Android"));
        innerMap.put("country", null);
        inputList.add(innerMap);

        // Expected
        expectedList = new ArrayList<>();
        expectedList.add("product-1");
        if (FILTER == FILTER_TO_STRING) {
            expectedList.add("product-2");
            expectedList.add("product-3");
        } else if (FILTER == FILTER_TO_NULL) {
            expectedList.add(null);
            expectedList.add(null);
        } else if (FILTER == DO_NOT_FILTER) {
            expectedList.add(new StringBuffer("product-2"));
            expectedList.add(new StringBuilder("product-3"));
        } else if (FILTER == FILTER_TO_DISCARD) {
            // Discarded
        }
        innerMap.clear();
        innerMap.put("device-name", "Redmi 1s");
        if (FILTER == FILTER_TO_STRING) {
            innerMap.put("device-os", "Android");
            innerMap.put("country", null);
        } else if (FILTER == FILTER_TO_NULL) {
            innerMap.put("device-os", null);
            innerMap.put("country", null);
        } else if (FILTER == DO_NOT_FILTER) {
            innerMap.put("device-os", new StringBuilder("Android"));
            innerMap.put("country", null);
        } else if (FILTER == FILTER_TO_DISCARD) {
            // Discarded
        }
        expectedList.add(innerMap);

        try {
            outputList = (ArrayList<Object>) DataType.cloneExternal(inputList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneExternalList", String.valueOf(expectedList), String.valueOf(outputList));

        try {
            outputList = (ArrayList<Object>) DataType.cloneExternal("Products Added", inputList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneExternalList with event", String.valueOf(expectedList), String.valueOf(outputList));
    }

    public void testCloneExternalMap() {
        inputHashMap = new HashMap<>();
        inputHashMap.put("key1", "value1");
        inputHashMap.put("key2", "value2");
        inputHashMap.put("key3", new StringBuilder("value3"));
        inputHashMap.put("key4", 25.0);
        inputHashMap.put("key5", new BigInteger("1000"));

        expectedHashMap = new HashMap<>();
        expectedHashMap.put("key1", "value1");
        expectedHashMap.put("key2", "value2");
        if (FILTER == FILTER_TO_STRING) {
            expectedHashMap.put("key3", "value3");
        } else if (FILTER == FILTER_TO_NULL) {
            expectedHashMap.put("key3", null);
        } else if (FILTER == DO_NOT_FILTER) {
            expectedHashMap.put("key3", new StringBuilder("value3"));
        } else if (FILTER == FILTER_TO_DISCARD) {
            // Discarded
        }
        expectedHashMap.put("key4", 25.0);
        expectedHashMap.put("key5", new BigInteger("1000"));

        try {
            outputHashMap = (HashMap<String, Object>) DataType.cloneExternal(inputHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneExternalMap", String.valueOf(expectedHashMap), String.valueOf(outputHashMap));

        try {
            outputHashMap = (HashMap<String, Object>) DataType.cloneExternal("test-event", inputHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneExternalMap with event", String.valueOf(expectedHashMap), String.valueOf(outputHashMap));
    }

    public void testCloneExternalMapInMap() {
        // Input
        inputHashMap = new HashMap<>();

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1234");
        innerMap.put("product-name", "coffee-cup");
        innerMap.put("product-price", 25);
        inputHashMap.put("product-1", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1235");
        innerMap.put("product-name", "pen-drive");
        innerMap.put("product-price", 50.00);
        inputHashMap.put("product-2", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1236");
        innerMap.put("product-name", new StringBuilder("laptop-bag"));
        innerMap.put("product-price", new BigDecimal("75.00"));
        inputHashMap.put("product-3", innerMap);

        // Expected
        expectedHashMap = new HashMap<>();

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1234");
        innerMap.put("product-name", "coffee-cup");
        innerMap.put("product-price", 25);
        expectedHashMap.put("product-1", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1235");
        innerMap.put("product-name", "pen-drive");
        innerMap.put("product-price", 50.00);
        expectedHashMap.put("product-2", innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1236");
        if (FILTER == FILTER_TO_STRING) {
            innerMap.put("product-name", "laptop-bag");
        } else if (FILTER == FILTER_TO_NULL) {
            innerMap.put("product-name", null);
        } else if (FILTER == DO_NOT_FILTER) {
            innerMap.put("product-name", new StringBuilder("laptop-bag"));
        } else if (FILTER == FILTER_TO_DISCARD) {
            // Discarded
        }
        innerMap.put("product-price", new BigDecimal("75.00"));
        expectedHashMap.put("product-3", innerMap);

        try {
            outputHashMap = (HashMap<String, Object>) DataType.cloneExternal(inputHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneExternalMapInMap", String.valueOf(expectedHashMap), String.valueOf(outputHashMap));
    }

    public void testCloneExternalMapInList() {
        // Input
        inputList = new ArrayList<>();

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1234");
        innerMap.put("product-name", "coffee-cup");
        innerMap.put("quantity", 1);
        inputList.add(innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1235");
        innerMap.put("product-name", "pen-drive");
        innerMap.put("quantity", 2);
        inputList.add(innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1236");
        innerMap.put("product-name", new StringBuffer("wrist-watch"));
        innerMap.put("quantity", 1);
        innerMap.put("discount-applied", false);
        inputList.add(innerMap);

        // Expected
        expectedList = new ArrayList<>();

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1234");
        innerMap.put("product-name", "coffee-cup");
        innerMap.put("quantity", 1);
        expectedList.add(innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1235");
        innerMap.put("product-name", "pen-drive");
        innerMap.put("quantity", 2);
        expectedList.add(innerMap);

        innerMap = new HashMap<>();
        innerMap.put("product-id", "1236");
        if (FILTER == FILTER_TO_STRING) {
            innerMap.put("product-name", "wrist-watch");
        } else if (FILTER == FILTER_TO_NULL) {
            innerMap.put("product-name", null);
        } else if (FILTER == DO_NOT_FILTER) {
            innerMap.put("product-name", new StringBuffer("wrist-watch"));
        } else if (FILTER == FILTER_TO_DISCARD) {
            // Discarded
        }
        innerMap.put("quantity", 1);
        innerMap.put("discount-applied", false);
        expectedList.add(innerMap);

        try {
            outputList = (List<Object>) DataType.cloneExternal(inputList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("testCloneExternalMapInList", String.valueOf(expectedList), String.valueOf(outputList));
    }
}
