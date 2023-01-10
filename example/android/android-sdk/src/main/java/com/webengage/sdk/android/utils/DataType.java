package com.webengage.sdk.android.utils;

import com.webengage.sdk.android.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public enum DataType {
    STRING,
    INTEGER,
    LONG,
    DOUBLE,
    BOOLEAN,
    LIST,
    MAP,
    DATE,
    NUMBER,
    UNKNOWN;

    public static DataType valueByString(String str) {
        try {
            return DataType.valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Filter types
     * used while cloning.
     */
    private static final int DO_NOT_FILTER = 0;  // Unsupported data types will be allowed.
    private static final int FILTER_TO_NULL = 1;  // Unsupported data types will be converted to null.
    private static final int FILTER_TO_STRING = 2;  // Unsupported data types will be converted to string using String.valueOf() method.
    private static final int FILTER_TO_DISCARD = 3;  // Unsupported data types will not be tracked, i.e. discarded.

    public String toString() {
        return this.name().toLowerCase();
    }


    public static DataType detect(Object data) {
        if (data instanceof Long) {
            return LONG;
        } else if (data instanceof Integer) {
            return INTEGER;
        } else if (data instanceof Boolean) {
            return BOOLEAN;
        } else if (data instanceof String) {
            return STRING;
        } else if (data instanceof Double) {
            return DOUBLE;
        } else if (data instanceof List<?>) {
            return LIST;
        } else if (data instanceof Map<?, ?>) {
            return MAP;
        } else if (data instanceof Date) {
            return DATE;
        } else if (data instanceof Number) {
            return NUMBER;
        }
        return DataType.UNKNOWN;
    }


    public static Object convert(Object input, DataType dataType, boolean shouldHandleTransit) throws Exception {
        DataType inputDataType = detect(input);
        switch (dataType) {
            case STRING:
                switch (inputDataType) {
                    case STRING:
                    case MAP:
                    case LIST:
                    case DATE:
                    case UNKNOWN:
                        return toJSONToken(input, shouldHandleTransit).toString();
                    case INTEGER:
                    case LONG:
                    case DOUBLE:
                    case NUMBER:
                    case BOOLEAN:
                        return input.toString();


                }
                break;
            case INTEGER:
                switch (inputDataType) {
                    case DOUBLE:
                    case INTEGER:
                    case LONG:
                    case NUMBER:
                        return ((Number) input).intValue();
                    case STRING:
                        return Integer.valueOf(input.toString());
                }
                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to Integer");
            case LONG:
                switch (inputDataType) {
                    case DOUBLE:
                    case INTEGER:
                    case LONG:
                    case NUMBER:
                        return ((Number) input).longValue();
                    case STRING:
                        return Long.valueOf(input.toString());
                }
                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to Long");
            case DOUBLE:
                switch (inputDataType) {
                    case DOUBLE:
                    case INTEGER:
                    case LONG:
                    case NUMBER:
                        return ((Number) input).doubleValue();
                    case STRING:
                        return Double.valueOf(input.toString());
                }
                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to Double");
            case BOOLEAN:
                switch (inputDataType) {
                    case STRING:
                        return Boolean.valueOf(input.toString());
                    case BOOLEAN:
                        return input;
                }
                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to Boolean");
            case LIST:
                switch (inputDataType) {
                    case STRING:
                        JsonParser jsonParser = new JsonParser(new ByteArrayInputStream(input.toString().getBytes()), shouldHandleTransit);
                        return jsonParser.getAsList();
                    case LIST:
                        return input;

                }

                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to List");
            case MAP:
                switch (inputDataType) {
                    case STRING:
                        JsonParser jsonParser = new JsonParser(new ByteArrayInputStream(input.toString().getBytes()), shouldHandleTransit);
                        return jsonParser.getAsMap();
                    case MAP:
                        return input;

                }

                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to Map");

            case DATE:
                switch (inputDataType) {
                    case STRING:
                        String temp = input.toString();
                        SimpleDateFormat simpleDateFormat = null;
                        if (shouldHandleTransit) {
                            simpleDateFormat = new SimpleDateFormat(WebEngageConstant.DATE_TRANSIT_ISO_FORMAT, Locale.US);

                        } else {
                            simpleDateFormat = new SimpleDateFormat(WebEngageConstant.DATE_ISO_FORMAT, Locale.US);
                        }
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = simpleDateFormat.parse(input.toString());
                        return date;

                    case DATE:
                        return input;

                }

            case NUMBER:
                switch (inputDataType) {
                    case INTEGER:
                    case DOUBLE:
                    case LONG:
                    case NUMBER:
                        return input;
                    case STRING:
                        try {
                            return Long.parseLong(input.toString());
                        } catch (NumberFormatException e) {
                            return Double.parseDouble(input.toString());
                        }
                }
                throw new IllegalArgumentException(inputDataType.toString() + " cannot be converted to Number");
        }

        throw new IllegalArgumentException("Unknown Data Type : " + dataType);
    }

    public static boolean isNumber(DataType dataType) {
        return dataType.equals(INTEGER) || dataType.equals(LONG) || dataType.equals(DOUBLE) || dataType.equals(NUMBER);
    }

    private static Object toJSONToken(Object input, boolean applyTransitFormat) throws Exception {
        DataType dataType = detect(input);
        if (dataType != null) {
            switch (dataType) {
                case STRING:
                    String temp = (String) input;
                    if (applyTransitFormat) {
                        if (!temp.startsWith("~t") && (temp.startsWith("~") || temp.startsWith("^") || temp.startsWith("`"))) {
                            return "~" + temp;
                        }
                    }
                    return temp;
                case INTEGER:
                case LONG:
                case DOUBLE:
                case NUMBER:
                case BOOLEAN:
                    return input;
                case DATE:
                    DateFormat dateFormat = null;
                    if (applyTransitFormat) {
                        dateFormat = new SimpleDateFormat(WebEngageConstant.DATE_TRANSIT_ISO_FORMAT, Locale.US);
                    } else {
                        dateFormat = new SimpleDateFormat(WebEngageConstant.DATE_ISO_FORMAT, Locale.US);
                    }
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return dateFormat.format(input);

                case MAP:
                    JSONObject jsonObject = new JSONObject();
                    Map<String, Object> map = (Map<String, Object>) input;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        Object value = toJSONToken(entry.getValue(), applyTransitFormat);
                        jsonObject.put((String) toJSONToken(entry.getKey(), applyTransitFormat), value);

                    }
                    return jsonObject;
                case LIST:
                    JSONArray jsonArray = new JSONArray();
                    List<Object> list = (List<Object>) input;
                    for (Object o : list) {
                        Object value = toJSONToken(o, applyTransitFormat);
                        jsonArray.put(value);
                    }
                    return jsonArray;
                case UNKNOWN:
                    if (input instanceof IMap) {
                        return toJSONToken(((IMap) input).toMap(), applyTransitFormat);
                    } else {
                        return JSONObject.NULL;
                    }
            }
        } else {
            if (input == null) {
                return JSONObject.NULL;
            }
        }
        throw new IllegalArgumentException("Unknown Data Type : " + dataType);
    }

    /**
     * Stores cloned data, event name and error if data type is changed.
     * Used to log error while tracking unsupported data types.
     * event: event name if data is an event attribute else will be null.
     * data: actual data object
     * key: value of key if data is value for a key in Map else will be null.
     * error: error statement to be logged if data is filtered else will be null.
     * :P
     */
    static class CloneData {
        private String event = null;
        private Object data = null;
        private List<String> keys = null;
        private String error = null;

        CloneData(String event, Object data) {
            this.event = event;
            this.data = data;
            keys = new ArrayList<>(2);
            error = null;
        }

        public String getEvent() {
            return event;
        }

        public Object getData() {
            return data;
        }

        public void removeKey(String key) {
            keys.remove(key);
        }

        public String getKeys() {
            if (keys.size() == 0) {
                return null;
            }
            StringBuilder keysFlow = new StringBuilder();
            for (String key : keys) {
                if (keysFlow.length() != 0) {
                    keysFlow.append(" in key ");
                }
                keysFlow.append("'").append(key).append("'");
            }
            return keysFlow.toString();
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public CloneData set(Object data, String error) {
            this.data = data;
            this.error = error;
            return this;
        }

        public CloneData set(Object data, String key, String error) {
            this.data = data;
            keys.add(0, key);
            this.error = error;
            return this;
        }
    }

    /**
     * Performs deep cloning on provided object.
     * This method must always be used when data enters into sdk from external apis
     * such as track or setScreenData.
     *
     * @param data
     * @param shouldFilter flag to indicate whether UNKNOWN data types should be rejected or not
     *                     true:  reject
     *                     false: pass
     *                     This flag must be false when cloning is done on some internal sdk data,
     *                     such as reading an returning cloned value from DataHolder,
     *                     because data type of internal data can be UNKNOWN such as AtomicBoolean.
     *                     <p>
     *                     This flag must always be true when data is read from external apis.
     * @return
     * @throws Exception ha, ha!!!
     */
    static Object clone(Object data, boolean shouldFilter, boolean shouldTruncate, int truncateLimit) throws Exception {
        DataType dataType = detect(data);
        switch (dataType) {
            case STRING:
                if (shouldTruncate) {
                    return WebEngageUtils.truncate(data.toString(), truncateLimit);
                }
            case INTEGER:
            case LONG:
            case DOUBLE:
            case NUMBER:
            case BOOLEAN:
                return data;


            case LIST:
                List<Object> list = (List<Object>) data;
                List<Object> clonedList = new ArrayList<Object>();
                Iterator<Object> iterator = list.iterator();
                while (iterator.hasNext()) {
                    clonedList.add(clone(iterator.next(), shouldFilter, shouldTruncate, 1000));
                }
                return clonedList;


            case MAP:
                Map<String, Object> map = (Map<String, Object>) data;
                Map<String, Object> clonedMap = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    clonedMap.put(clone(entry.getKey(), shouldFilter, shouldTruncate, 50).toString(), clone(entry.getValue(), shouldFilter, shouldTruncate, 1000));
                }
                return clonedMap;


            case DATE:
                return new Date(((Date) data).getTime());

            default:
            case UNKNOWN:
                if (shouldFilter) {
                    return null;
                } else {
                    return data;//clone, take care of atomic boolean
                }

        }
    }

    /**
     * Performs deep cloning and filtering on provided object.
     * This method must always be used when data enters into sdk from external apis
     * such as track or setScreenData.
     *
     * @param cloneData
     * @param filterType
     * @param shouldTruncate
     * @param truncateLimit
     * @return cloned data
     * @throws Exception
     */
    static CloneData clone(CloneData cloneData, int filterType, boolean shouldTruncate, int truncateLimit) throws Exception {
        DataType dataType = detect(cloneData.getData());
        switch (dataType) {
            case STRING:
                if (shouldTruncate) {
                    return cloneData.set(WebEngageUtils.truncate((cloneData.getData()).toString(), truncateLimit), null);
                }

            case INTEGER:
            case LONG:
            case DOUBLE:
            case NUMBER:
            case BOOLEAN:
                cloneData.setError(null);
                return cloneData;

            case LIST:
                List<Object> list = (List<Object>) cloneData.getData();
                List<Object> clonedList = new ArrayList<Object>();
                Iterator<Object> iterator = list.iterator();
                while (iterator.hasNext()) {
                    cloneData = clone(cloneData.set(iterator.next(), null), filterType, shouldTruncate, 1000);
                    String error;
                    if ((error = cloneData.getError()) != null) {
                        String key = cloneData.getKeys();
                        String event;
                        if ((event = cloneData.getEvent()) != null) {
                            if (key != null) {
                                Logger.d(WebEngageConstant.TAG, "Invalid value for key " + key + " in event '" + event + "'\n" + error);
                            } else {
                                Logger.d(WebEngageConstant.TAG, "Invalid attribute in event '" + event + "'\n" + error);
                            }
                        } else {
                            if (key != null) {
                                Logger.d(WebEngageConstant.TAG, "Invalid value for key " + key + "\n" + error);
                            } else {
                                Logger.d(WebEngageConstant.TAG, error);
                            }
                        }

                        if (filterType != FILTER_TO_DISCARD) {
                            clonedList.add(cloneData.getData());
                        }
                    } else {
                        clonedList.add(cloneData.getData());
                    }
                }
                return cloneData.set(clonedList, null);

            case MAP:
                Map<String, Object> map = (Map<String, Object>) cloneData.getData();
                Map<String, Object> clonedMap = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    cloneData = clone(cloneData.set(entry.getKey(), null), filterType, shouldTruncate, 50);
                    String key = String.valueOf(cloneData.getData());
                    cloneData = clone(cloneData.set(entry.getValue(), key, null), filterType, shouldTruncate, 1000);
                    String error;
                    if ((error = cloneData.getError()) != null) {
                        if (cloneData.getEvent() != null) {
                            Logger.d(WebEngageConstant.TAG, "Invalid value for key " + cloneData.getKeys() + " in event '" + cloneData.getEvent() + "'\n" + error);
                        } else {
                            Logger.d(WebEngageConstant.TAG, "Invalid value for key " + cloneData.getKeys() + "\n" + error);
                        }

                        if (filterType != FILTER_TO_DISCARD) {
                            clonedMap.put(key, cloneData.getData());
                        }
                    } else {
                        clonedMap.put(key, cloneData.getData());
                    }
                    cloneData.removeKey(key);
                }
                return cloneData.set(clonedMap, null);

            case DATE:
                return cloneData.set(new Date(((Date) cloneData.getData()).getTime()), null);

            default:
            case UNKNOWN:
                Object data = cloneData.getData();
                String error = null;
                switch (filterType) {
                    case DO_NOT_FILTER:
                        return cloneData.set(data, null);
                    case FILTER_TO_NULL:
                        if (data != null) {
                            error = new StringBuilder().append("Invalid data type '").append( data.getClass().getSimpleName()).append("'. Must be one of [String, Number, Boolean, List, Map, Date].\nConverted value to null.").toString();
                        }
                        return cloneData.set(null, error);
                    case FILTER_TO_STRING:
                        if (data != null) {
                            error = new StringBuilder().append("Invalid data type '").append( data.getClass().getSimpleName()).append("'. Must be one of [String, Number, Boolean, List, Map, Date].\nConverted value to String.").toString();
                            cloneData.set(String.valueOf(data), error);
                        } else {
                            error = "Value is null.";
                            cloneData.set(null, error);
                        }
                        return cloneData;
                    default:
                    case FILTER_TO_DISCARD:
                        if (data != null) {
                            error = new StringBuilder().append("Invalid data type '").append( data.getClass().getSimpleName()).append("'. Must be one of [String, Number, Boolean, List, Map, Date].\nDiscarded value.").toString();
                        } else {
                            error = "Value is null.\nDiscarded value";
                        }
                        return cloneData.set(String.valueOf(data), error);
                }
        }
    }

    public static Object cloneInternal(Object data) throws Exception {
        return clone(data, false, false, Integer.MAX_VALUE); // shouldfilter is false to handle other unknown data types such as AtomicBoolean
    }

    public static Object cloneExternal(Object data) throws Exception {
        return cloneExternal(null, data);
    }

    public static Object cloneExternal(String event, Object data) throws Exception {
        CloneData objectData = clone(new CloneData(event, data), FILTER_TO_STRING, true, Integer.MAX_VALUE);
        String error;
        if ((error = objectData.getError()) != null) {
            Logger.e(WebEngageConstant.TAG, error);
        }
        return objectData.getData();
    }
}
