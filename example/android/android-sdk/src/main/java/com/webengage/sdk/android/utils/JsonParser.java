package com.webengage.sdk.android.utils;


import android.util.JsonReader;
import android.util.JsonToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

class JsonParser {
    JsonReader jsonReader = null;
    Object value = null;
    boolean shouldHandleTransit = false;
    static int ISODateStringLength = "yyyy-MM-ddTHH:mm:ss.SSSZ".length();

    JsonParser(InputStream inputStream, boolean shouldHandleTransit) throws Exception {
        jsonReader = new JsonReader(new InputStreamReader(inputStream));
        this.shouldHandleTransit = shouldHandleTransit;
        value = readNext();
        jsonReader.close();

    }

    Map<String, Object> getAsMap() {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    List<Object> getAsList() {
        if (value instanceof List) {
            return (List<Object>) value;
        }
        return null;
    }

    private Object readNext() throws Exception {
        JsonToken jsonToken = jsonReader.peek();
        switch (jsonToken) {
            case BEGIN_OBJECT:
                jsonReader.beginObject();
                Map<String, Object> map = new HashMap<String, Object>();
                while (jsonReader.hasNext()) {
                    map.put((String) readNext(), readNext());
                }
                readNext();
                return map;

            case BEGIN_ARRAY:
                jsonReader.beginArray();
                List<Object> list = new ArrayList<Object>();
                while (jsonReader.hasNext()) {
                    list.add(readNext());
                }
                readNext();
                return list;

            case END_ARRAY:
                jsonReader.endArray();
                break;

            case END_OBJECT:
                jsonReader.endObject();
                break;

            case NAME:
                String temp = jsonReader.nextName();
                if (shouldHandleTransit) {
                    return handleTransitString(temp);
                } else {
                    return parseISODateString(temp);
                }

            case STRING:
                temp = jsonReader.nextString();
                if (shouldHandleTransit) {
                    return handleTransitString(temp);
                } else {
                    return parseISODateString(temp);
                }

            case NUMBER:
                temp = jsonReader.nextString();
                try {
                    return Long.parseLong(temp);
                } catch (NumberFormatException e) {
                    return Double.parseDouble(temp);
                }
            case NULL:
                jsonReader.nextNull();
                return null;
            case BOOLEAN:
                return jsonReader.nextBoolean();
        }
        return null;
    }

    private Object handleTransitString(String temp) {
        if (temp.startsWith("~t")) {
            try {
                return DataType.convert(temp, DataType.DATE, true);
            } catch (Exception e) {
                return null;
            }
        } else if (temp.startsWith("~") && temp.length() > 1) {
            return temp.substring(1);
        } else {
            return temp;
        }
    }

    private Object parseISODateString(String temp) {
        if (temp == null) {
            return null;
        }
        if (temp.length() == ISODateStringLength) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebEngageConstant.DATE_ISO_FORMAT, Locale.US);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return simpleDateFormat.parse(temp);
            } catch (Exception e) {
                return temp;
            }
        }
        return temp;

    }


}
