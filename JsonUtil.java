package com.customerapp.server;

import java.util.HashMap;
import java.util.Map;

/**
 * A tiny JSON parser for flat objects like {"firstName":"John","lastName":"Smith"}.
 * This avoids pulling in an external JSON library for a small demo project.
 * Not meant for nested/complex JSON.
 */
public class JsonUtil {

    public static Map<String, String> parseFlatObject(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        String body = json.trim();
        if (body.startsWith("{")) body = body.substring(1);
        if (body.endsWith("}")) body = body.substring(0, body.length() - 1);

        int i = 0;
        int len = body.length();
        while (i < len) {
            // skip whitespace/commas
            while (i < len && (body.charAt(i) == ',' || Character.isWhitespace(body.charAt(i)))) i++;
            if (i >= len) break;

            String key = readJsonString(body, i);
            i += quotedLength(body, i);
            while (i < len && (body.charAt(i) == ':' || Character.isWhitespace(body.charAt(i)))) i++;

            String value;
            if (i < len && body.charAt(i) == '"') {
                value = readJsonString(body, i);
                i += quotedLength(body, i);
            } else {
                int start = i;
                while (i < len && body.charAt(i) != ',' && body.charAt(i) != '}') i++;
                value = body.substring(start, i).trim();
            }
            map.put(key, value);
        }
        return map;
    }

    private static String readJsonString(String s, int start) {
        int i = start;
        if (s.charAt(i) != '"') return "";
        i++;
        StringBuilder sb = new StringBuilder();
        while (i < s.length() && s.charAt(i) != '"') {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                i++;
                sb.append(s.charAt(i));
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    private static int quotedLength(String s, int start) {
        int i = start;
        if (s.charAt(i) != '"') return 0;
        i++;
        int count = 1;
        while (i < s.length() && s.charAt(i) != '"') {
            if (s.charAt(i) == '\\') { i++; count++; }
            i++;
            count++;
        }
        count++; // closing quote
        return count;
    }
}
