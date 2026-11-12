package com.cmvapparel.dashboard.api;

import java.util.List;
import java.util.Map;

// Turns maps, lists, text and numbers into a JSON string
public class Json {

    public static String write(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Double d) {
            return String.valueOf(Math.round(d * 100) / 100.0); // 2 decimal places
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringBuilder json = new StringBuilder("{");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (json.length() > 1) {
                    json.append(",");
                }
                json.append(quote(entry.getKey().toString())).append(":").append(write(entry.getValue()));
            }
            return json.append("}").toString();
        }
        if (value instanceof List<?> list) {
            StringBuilder json = new StringBuilder("[");
            for (Object item : list) {
                if (json.length() > 1) {
                    json.append(",");
                }
                json.append(write(item));
            }
            return json.append("]").toString();
        }
        return quote(value.toString());
    }

    private static String quote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
