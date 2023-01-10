package com.webengage.sdk.android.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class WebEngageUtilsTest {
    private static final String DATE_BIRTHDAY_FORMAT = "yyyy-MM-dd";
    private static final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String TIMEZONE = "UTC";

    @BeforeClass
    public static void setUp() {
        // Nothing to setup
    }

    @Test
    public void testMethods() {
        testAreEqual();
    }

    private void testAreEqual() {
        // Null
        testAreEqual(null, null, true, "test areEqual: Null");

        // Boolean
        testAreEqual(true, true, true, "test areEqual: Boolean (1)");
        testAreEqual(true, false, false, "test areEqual: Boolean (2)");
        testAreEqual(new Boolean(true), Boolean.TRUE, true, "test areEqual: Boolean (3)");
        testAreEqual(new Boolean(false), true, false, "test areEqual: Boolean (4)");

        // Strings
        testAreEqual("abc", "abc", true, "test areEqual: String (1)");
        testAreEqual("abc", "abd", false, "test areEqual: String (2)");
        testAreEqual("", "", true, "test areEqual: String (3)");
        testAreEqual("", " ", false, "test areEqual: String (4)");
        testAreEqual(new StringBuilder("abc").append("d"), "abcd", true, "test areEqual: String (5)");
        testAreEqual(new StringBuffer("abcd"), "abcd", true, "test areEqual: String (6)");

        // Numbers
        testAreEqual(0.01, 0.01, true, "areEqual: Numbers test 1");
        testAreEqual(0, 0.0, true, "areEqual: Numbers test 2");
        testAreEqual(0, 0f, true, "areEqual: Numbers test 3");
        testAreEqual(0, 0d, true, "areEqual: Numbers test 4");
        testAreEqual(0f, 0d, true, "areEqual: Numbers test 5");

        testAreEqual(1, 1, true, "areEqual: Numbers test 6");
        testAreEqual(1, 0, false, "areEqual: Numbers test 7");
        testAreEqual(1, 1.0, true, "areEqual: Numbers test 8");
        testAreEqual(1, -1, false, "areEqual: Numbers test 9");
        testAreEqual(1.0, 1.00, true, "areEqual: Numbers test 10");
        testAreEqual(1.0, 0.1, false, "areEqual: Numbers test 11");
        testAreEqual(0.00001, 0.00001, true, "areEqual: Numbers test 12");
        testAreEqual(0.00001, 0.00002, false, "areEqual: Numbers test 13");
        testAreEqual(1.000f, 1.00000d, true, "areEqual: Numbers test 14");
        testAreEqual(1.000f, 1.000001d, false, "areEqual: Numbers test 15");
        testAreEqual(1.000001f, 1.000002d, false, "areEqual: Numbers test 16");
        testAreEqual(125000, 125000L, true, "areEqual: Numbers test 17");
        testAreEqual(125000, 125000.000d, true, "areEqual: Numbers test 18");
        testAreEqual(125000L, 125000d, true, "areEqual: Numbers test 19");
        testAreEqual(125000, 125001, false, "areEqual: Numbers test 20");

        testAreEqual(2147483648L, 2147483648L, true, "areEqual: Numbers test 21");
        testAreEqual(2147483648L, 2147483649L, false, "areEqual: Numbers test 22");
        testAreEqual(2147483648L, 2147483648.000d, true, "areEqual: Numbers test 23");
        testAreEqual(2147483648L, 2147483648.001d, false, "areEqual: Numbers test 24");

        testAreEqual(Integer.MAX_VALUE, new BigInteger(String.valueOf(Integer.MAX_VALUE)), true, "areEqual: Numbers test 25");
        testAreEqual(214748360, new BigInteger("214748360"), true, "areEqual: Numbers test 26");
        testAreEqual(214748360, new BigDecimal("214748360"), true, "areEqual: Numbers test 27");
        testAreEqual(214748360, new BigDecimal("214748361"), false, "areEqual: Numbers test 28");
        testAreEqual(new BigInteger("2147483648"), new BigDecimal("2147483648.0"), true, "areEqual: Numbers test 29");
        testAreEqual(new BigDecimal("2147483648"), new BigDecimal("2147483648"), true, "areEqual: Numbers test 30");
        testAreEqual(new BigDecimal("2147483648"), new BigDecimal("2147483649"), false, "areEqual: Numbers test 31");

        // Dates
        Date now = new Date();
        testAreEqual(now, now, true, "test areEqual: Date");
        testAreEqual(getDate("2019-05-21", DATE_BIRTHDAY_FORMAT), getDate("2019-05-21", DATE_BIRTHDAY_FORMAT), true, "test areEqual: Dates (1)");
        testAreEqual(getDate("2019-05-21", DATE_BIRTHDAY_FORMAT), getDate("2019-05-22", DATE_BIRTHDAY_FORMAT), false, "test areEqual: Dates (2)");
        testAreEqual(getDate("2019-05-21T12:00:00.000Z", DATE_ISO_FORMAT), getDate("2019-05-21T12:00:00.000Z", DATE_ISO_FORMAT), true, "test areEqual: Dates (3)");
        testAreEqual(getDate("2019-05-21T00:00:00.000Z", DATE_ISO_FORMAT), getDate("2019-05-21", DATE_BIRTHDAY_FORMAT), true, "test areEqual: Dates (4)");
        testAreEqual(getDate("2019-05-21T12:00:00.0Z", DATE_ISO_FORMAT), getDate("2019-05-21T12:00:00.000Z", DATE_ISO_FORMAT), true, "test areEqual: Dates (5)");
        testAreEqual(getDate("2019-05-21T12:00:00.001Z", DATE_ISO_FORMAT), getDate("2019-05-21T12:00:00.002Z", DATE_ISO_FORMAT), false, "test areEqual: Dates (5)");

        // Maps
        Map<String, Object> map1 = getMap();
        Map<String, Object> map2 = getMap();
        testAreEqual(map1, map2, true, "test areEqual: Maps (1)");

        Map<String, Object> map3 = getMap();
        map3.put("key3", false);
        testAreEqual(map1, map3, false, "test areEqual: Maps (2)");

        // Lists
        List<Object> list1 = getList();
        List<Object> list2 = getList();
        testAreEqual(list1, list1, true, "test areEqual: Lists (1)");
        testAreEqual(list1, list2, true, "test areEqual: Lists (2)");

        List<Object> list3 = getList();
        list3.set(1, "abc");
        testAreEqual(list1, list3, false, "test areEqual: Lists (3)");

        List<Object> list4 = getList();
        list4.remove(1);
        testAreEqual(list1, list4, false, "test areEqual: Lists (4)");

        // Map in Map
        Map<String, Object> bigMap1 = new HashMap<>();
        bigMap1.put("key1", map1);
        bigMap1.put("key2", "abcd");

        Map<String, Object> bigMap2 = new HashMap<>();
        bigMap2.put("key1", map2);
        bigMap2.put("key2", "abcd");
        testAreEqual(bigMap1, bigMap2, true, "test areEqual: Map in Map (1)");

        Map<String, Object> bigMap3 = new HashMap<>();
        bigMap3.put("key1", map3);
        bigMap3.put("key2", "abcd");
        testAreEqual(bigMap1, bigMap3, false, "test areEqual: Map in Map (2)");

        // List in Map
        bigMap1.put("key3", list1);
        bigMap2.put("key3", list2);
        testAreEqual(bigMap1, bigMap2, true, "test areEqual: List in Map (1)");

        Map<String, Object> bigMap4 = new HashMap<>(bigMap1);
        bigMap4.put("key3", list3);
        testAreEqual(bigMap1, bigMap4, false, "test areEqual: List in Map (2)");
    }

    private void testAreEqual(Object o1, Object o2, boolean expected, String msg) {
        boolean actual = WebEngageUtils.areEqual(o1, o2);
        assertEquals(msg + ": obj1: " + o1 + ", obj2: " + o2, expected, actual);
    }

    private Date getDate(String input, String format) {
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        try {
            date = simpleDateFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", null);
        map.put("key2", "abcd");
        map.put("key3", true);
        map.put("key4", 1);
        map.put("key5", 12.25);
        map.put("key6", 125L);
        return map;
    }

    private List<Object> getList() {
        List<Object> list = new ArrayList<>();
        list.add(null);
        list.add("abcd");
        list.add(true);
        list.add(1);
        list.add(12.25);
        list.add(125L);
        return list;
    }
}
