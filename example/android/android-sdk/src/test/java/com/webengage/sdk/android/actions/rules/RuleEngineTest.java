package com.webengage.sdk.android.actions.rules;

import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.UserDeviceAttribute;
import com.webengage.sdk.android.UserSystemAttribute;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@PrepareForTest(RuleExecutorImpl.class)
@RunWith(PowerMockRunner.class)
public class RuleEngineTest {
    private static final String MOCK_CAMPAIGN_ID = "test_campaign";

    private DataHolder dataHolder;

    private RuleExecutorImpl ruleExecutor = null;

    @Before
    public void setUp() {
        Map<String, Object> mockContainer = new HashMap<>();

        Map<String, Object> deviceAttribute = new HashMap<>();
        deviceAttribute.put(UserDeviceAttribute.REFERRER, "direct");
        deviceAttribute.put(UserDeviceAttribute.CITY, "Mumbai");
        deviceAttribute.put(UserDeviceAttribute.TIME_SPENT, 60000L);
        deviceAttribute.put("sdk_id", BuildConfig.SDK_ID);

        final String userIdentifier = "ashwin";
        Map<String, Object> userSystemAttribute = new HashMap<String, Object>();
        userSystemAttribute.put(UserSystemAttribute.FIRST_NAME.toString(), "ashwin");
        userSystemAttribute.put(UserSystemAttribute.PHONE.toString(), "9867271986");
        userSystemAttribute.put(UserSystemAttribute.TIME_SPENT.toString(), 60000L);
        userSystemAttribute.put(UserSystemAttribute.CITY.toString(), "Mumbai");
        userSystemAttribute.put(UserSystemAttribute.REFERRER.toString(), "direct");
        userSystemAttribute.put("cuid", userIdentifier);

        mockContainer.put("user", userSystemAttribute);
        mockContainer.put("android", deviceAttribute);

        dataHolder = DataHolder.get();
        try {
            Method setSilentDataMethod = dataHolder.getClass().getDeclaredMethod("silentSetData", new Class[]{Map.class});
            setSilentDataMethod.setAccessible(true);
            setSilentDataMethod.invoke(dataHolder, mockContainer);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        ruleExecutor = new RuleExecutorImpl();

        printDataHolder();
    }

    public void printDataHolder() {
        if (dataHolder == null) {
            System.out.println("DataHolder null");
        } else {
            System.out.println("DataHolder container: " + dataHolder.container);
        }
    }

    @Test
    public void testMethods() {
        // all true test
        true_test();
        false_test();

        // operator tests
        not_test();

        // we tests
        we_in_test();
    }

    public void true_test() {
        ruleExecutor = PowerMockito.spy(new RuleExecutorImpl());

        String sessionRule = "true";
        String pageRule = "true";
        String eventRule = "true";
        Rule rule = new Rule(sessionRule, pageRule, eventRule);
        Map<String, Rule> ruleMap = new LinkedHashMap<>();
        ruleMap.put("test_campaign", rule);
        ruleExecutor.setRuleMap(ruleMap);

        // expected
        List<String> expectedIds = new ArrayList<>();
        expectedIds.add(MOCK_CAMPAIGN_ID);

        // actual
        List<String> actualIds = ruleExecutor.evaluateRulesByCategory(WebEngageConstant.RuleCategory.SESSION_RULE);  //null;

        assertNotNull(actualIds);
        assertEquals("true_test", expectedIds, actualIds);
    }

    public void false_test() {
        ruleExecutor = PowerMockito.spy(new RuleExecutorImpl());

        String sessionRule = "false";
        String pageRule = "true";
        String eventRule = "true";
        Rule rule = new Rule(sessionRule, pageRule, eventRule);
        Map<String, Rule> ruleMap = new LinkedHashMap<>();
        ruleMap.put(MOCK_CAMPAIGN_ID, rule);
        ruleExecutor.setRuleMap(ruleMap);

        // expected
        List<String> expectedIds = new ArrayList<>();
        expectedIds.add(MOCK_CAMPAIGN_ID);

        // actual
        List<String> actualIds = ruleExecutor.evaluateRulesByCategory(WebEngageConstant.RuleCategory.SESSION_RULE);  //null;

        assertNotEquals("false_test", expectedIds, actualIds);
    }

    public void not_test() {
        not_test(BuildConfig.SDK_ID);
        not_test(BuildConfig.SDK_ID + 1);
    }

    public void not_test(int sdk_id) {
        String sessionRule = "( $we_getData(\"android\"->\"sdk_id\") != " + sdk_id + ")";
        String pageRule = "true";
        String eventRule = "true";
        Rule rule = new Rule(sessionRule, pageRule, eventRule);
        Map<String, Rule> ruleMap = new LinkedHashMap<>();
        ruleMap.put(MOCK_CAMPAIGN_ID, rule);
        ruleExecutor.setRuleMap(ruleMap);

        // expected
        List<String> expectedIds = new ArrayList<>();
        expectedIds.add(MOCK_CAMPAIGN_ID);

        // actual
        List<String> actualIds = ruleExecutor.evaluateRulesByCategory(WebEngageConstant.RuleCategory.SESSION_RULE);

        if (sdk_id != BuildConfig.SDK_ID) {
            assertEquals("not_test", expectedIds, actualIds);
        } else {
            assertNotEquals("not_test", expectedIds, actualIds);
        }
    }

    public void we_in_test() {
        String cuidList = "\"user_0\"";
        for (int i = 1; i <= 10; i++) {
            cuidList += ", \"user_" + i + "\"";
        }
        cuidList += ", \"ashwin\"";

        String sessionRule = "( $we_getData(\"user\"->\"cuid\") $we_in [" + cuidList + "] )";
        String pageRule = "true";
        String eventRule = "true";
        Rule rule = new Rule(sessionRule, pageRule, eventRule);
        Map<String, Rule> ruleMap = new LinkedHashMap<>();
        ruleMap.put(MOCK_CAMPAIGN_ID, rule);
        ruleExecutor.setRuleMap(ruleMap);

        // expected
        List<String> expectedIds = new ArrayList<>();
        expectedIds.add(MOCK_CAMPAIGN_ID);

        // actual
        List<String> actualIds = ruleExecutor.evaluateRulesByCategory(WebEngageConstant.RuleCategory.SESSION_RULE);

        assertEquals("we_in_test", expectedIds, actualIds);
    }
}
