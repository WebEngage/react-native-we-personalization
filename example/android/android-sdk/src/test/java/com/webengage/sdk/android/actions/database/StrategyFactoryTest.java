package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StrategyFactory.class)
public class StrategyFactoryTest {

    private Context context;
    private ReportingStatistics reportingStatistics;
    private StrategyFactory strategyFactory;
    private BufferStrategy bufferStrategy;
    private NetworkStrategy networkStrategy;
    private FlushStrategy flushStrategy;
    private BufferFlushStrategy bufferFlushStrategy;
    private BufferNetworkStrategy bufferNetworkStrategy;
    private EventPayload eventPayload;

    // Test data
    private boolean[] booleanArray = {true, false};
    private String[] categoriesArray = {WebEngageConstant.APPLICATION, WebEngageConstant.SYSTEM};
    private int[] prioritiesArray = {Integer.MIN_VALUE, -1, 0, 1, 2, 3, Integer.MAX_VALUE};
    private int[] networkReportingFailureCountsArray = {Integer.MIN_VALUE, -1, 0, 1, 2, 3, 4, 5, 6, 7, Integer.MAX_VALUE};
    private ReportingStrategy[] reportingStrategiesArray = {ReportingStrategy.BUFFER, ReportingStrategy.FORCE_SYNC};
    private String[] systemEventNamesArray = {EventName.APP_CRASHED, EventName.GEOFENCE_TRANSITION, EventName.NOTIFICATION_VIEW, " ", "", null};
    private String[] applicationEventNamesArray = {"Added to Cart", "Added to Wishlist", " ", "", null};
    private static List<Map> extraDataArray;
    static {
        extraDataArray = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put(WebEngageConstant.HIGH_REPORTING_PRIORITY, false);
        extraDataArray.add(map);
        map = new HashMap<>();
        map.put(WebEngageConstant.HIGH_REPORTING_PRIORITY, true);
        extraDataArray.add(map);
        extraDataArray.add(null);
    }

    @Before
    public void setUp() {
        context = mock(Context.class);
        bufferStrategy = new BufferStrategy(context);
        networkStrategy = new NetworkStrategy(context);
        flushStrategy = new FlushStrategy(context, networkStrategy);
        bufferFlushStrategy = new BufferFlushStrategy(bufferStrategy, flushStrategy);
        bufferNetworkStrategy = new BufferNetworkStrategy(bufferStrategy, networkStrategy, context);
    }

    @Test
    public void testMethods() {
        // Tests for getPriority(EventPayload eventPayload);
        testGetPriority();

        // Tests for getStrategy(int priority)
        testGetStrategyWithPriority();

        // Tests for getStrategy(EventPayload eventPayload)
        testGetStrategyWithEventPayload();
    }


    public void testGetStrategyWithPriority() {
        for (int priority : prioritiesArray) {
            for (boolean lastNetworkReportStatus: booleanArray) {
                for (int networkReportingFailureCount : networkReportingFailureCountsArray) {
                    for (ReportingStrategy currentReportingStrategy : reportingStrategiesArray) {
                        try {
                            testGetStrategyWithPriority(priority, lastNetworkReportStatus, networkReportingFailureCount, currentReportingStrategy);
                        } catch (Exception e) {
                            System.out.println("Exception in testGetStrategyWithPriority(priority: " + priority + ", lastNetworkReportStatus: " + lastNetworkReportStatus + ", networkReportingFailureCount: " + networkReportingFailureCount + ", currentReportingStrategy: " + currentReportingStrategy.toString() + ")\n");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public Strategy testGetStrategyWithPriority(int priority, boolean lastNetworkReportStatus, int networkReportFailureCount, ReportingStrategy currentReportingStrategy) throws Exception {
        // Mock ReportingStatistics
        reportingStatistics = mock(ReportingStatistics.class);
        when(reportingStatistics.getLastNetworkReportStatus()).thenReturn(lastNetworkReportStatus);
        when(reportingStatistics.getNetworkReportFailureCount()).thenReturn(networkReportFailureCount);

        // Spy on StrategyFactory
        strategyFactory = PowerMockito.spy(new StrategyFactory(reportingStatistics, context));

        // Mock private method getCurrentReportingStrategy
        when(strategyFactory, method(StrategyFactory.class, "getCurrentReportingStrategy")).withNoArguments().thenReturn(currentReportingStrategy);

        // Actual
        Strategy actual = Whitebox.invokeMethod(strategyFactory, "getStrategy", priority);

        // Expected
        Strategy expected;
        if (!lastNetworkReportStatus && networkReportFailureCount > 5) {
            expected = bufferStrategy;
        } else if (priority >= WebEngageConstant.PRIORITY_HIGH || currentReportingStrategy == ReportingStrategy.FORCE_SYNC) {
            expected = bufferFlushStrategy;
        } else {
            expected = bufferNetworkStrategy;
        }

        // Check results
        assertNotNull(actual);
        assertEquals("Error in testGetStrategyWithPriority(priority: " + priority + ", lastNetworkReportStatus: " + lastNetworkReportStatus + ", networkReportFailureCount: " + networkReportFailureCount + ", currentReportingStrategy: " + currentReportingStrategy.toString() + ")", expected.getClass(), actual.getClass());

        return actual;
    }

    public void testGetPriority() {
        String[] eventNamesArray;
        for (boolean isEventPayloadNull : booleanArray) {
            for (String category : categoriesArray) {
                for (Map extraData : extraDataArray) {
                    eventNamesArray = (category.equals(WebEngageConstant.SYSTEM)) ? systemEventNamesArray : applicationEventNamesArray;
                    for (String eventName : eventNamesArray) {
                        try {
                            testGetPriority(isEventPayloadNull, category, extraData, eventName);
                        } catch (Exception e) {
                            System.out.println("Exception in testGetPriority(isEventPayloadNull: " + isEventPayloadNull + ", category: " + category + ", extraData: " + extraData + ", eventName: " + eventName + ")");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public int testGetPriority(boolean isEventPayloadNull, String category, Map extraData, String eventName) throws Exception {
        // Mock EventPayload
        eventPayload = mock(EventPayload.class);
        if (!isEventPayloadNull) {
            when(eventPayload.getCategory()).thenReturn(category);
            when(eventPayload.getExtraData()).thenReturn(extraData);
            when(eventPayload.getEventName()).thenReturn(eventName);
        } else {
            eventPayload = null;
        }

        // Mock ReportingStatistics
        reportingStatistics = mock(ReportingStatistics.class);

        // Spy on StrategyFactory
        strategyFactory = PowerMockito.spy(new StrategyFactory(reportingStatistics, context));

        // Actual
        int actual = Whitebox.invokeMethod(strategyFactory, "getPriority", eventPayload);

        // Expected
        int expected;
        if (isEventPayloadNull) {
            expected = WebEngageConstant.PRIORITY_LOW;
        } else if (category.equals(WebEngageConstant.APPLICATION) && extraData != null && (Boolean) extraData.get(WebEngageConstant.HIGH_REPORTING_PRIORITY)) {
            expected = WebEngageConstant.PRIORITY_HIGH;
        } else if (WebEngageConstant.priorityMap.get(eventName) != null) {
            expected = WebEngageConstant.priorityMap.get(eventName);
        } else {
            expected = WebEngageConstant.PRIORITY_LOW;
        }

        // Check results
        assertEquals("testGetPriority(isEventPayloadNull: " + isEventPayloadNull + ", category: " + category + ", extraData: " + extraData + ", eventName: " + eventName + ")", expected, actual);

        return actual;
    }

    public void testGetStrategyWithEventPayload() {
        String[] eventNamesArray;
        for (boolean isEventPayloadNull : booleanArray) {
            for (String category : categoriesArray) {
                for (Map extraData : extraDataArray) {
                    eventNamesArray = (category.equals(WebEngageConstant.SYSTEM)) ? systemEventNamesArray : applicationEventNamesArray;
                    for (String eventName : eventNamesArray) {
                        for (boolean lastNetworkReportStatus : booleanArray) {
                            for (int networkReportFailureCount : networkReportingFailureCountsArray) {
                                for (ReportingStrategy currentReportingStrategy : reportingStrategiesArray) {
                                    try {
                                        testGetStrategyWithEventPayload(isEventPayloadNull, category, extraData, eventName, lastNetworkReportStatus, networkReportFailureCount, currentReportingStrategy);
                                    } catch (Exception e) {
                                        System.out.println("Exception in testGetStrategyWithEventPayload(isEventPayloadNull: " + isEventPayloadNull + ", category: " + category + ", extraData: " + extraData + ", eventName: " + eventName + ", lastNetworkReportStatus: " + lastNetworkReportStatus + ", networkReportFailureCount: " + networkReportFailureCount + ", currentReportingStrategy: " + currentReportingStrategy + ")");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void testGetStrategyWithEventPayload(boolean isEventPayloadNull, String category, Map extraData, String eventName, boolean lastNetworkReportStatus, int networkReportFailureCount, ReportingStrategy currentReportingStrategy) throws Exception {
        // Get priority
        int priority = testGetPriority(isEventPayloadNull, category, extraData, eventName);

        // Actual
        Strategy actual = testGetStrategyWithPriority(priority, lastNetworkReportStatus, networkReportFailureCount, currentReportingStrategy); //Whitebox.invokeMethod(strategyFactory, "getStrategy", eventPayload);

        // Expected
        Strategy expected;
        if (!lastNetworkReportStatus && networkReportFailureCount > 5) {
            expected = bufferStrategy;
        } else if (priority >= WebEngageConstant.PRIORITY_HIGH || currentReportingStrategy == ReportingStrategy.FORCE_SYNC) {
            expected = bufferFlushStrategy;
        } else {
            expected = bufferNetworkStrategy;
        }

        // Check results
        assertNotNull(actual);
        assertEquals("Error in testGetStrategyWithEventPayload(isEventPayloadNull: " + isEventPayloadNull + ", category: " + category + ", extraData: " + extraData + ", eventName: " + eventName + ", lastNetworkReportStatus: " + lastNetworkReportStatus + ", networkReportFailureCount: " + networkReportFailureCount + ", currentReportingStrategy: " + currentReportingStrategy + ")", expected.getClass(), actual.getClass());
    }

}
