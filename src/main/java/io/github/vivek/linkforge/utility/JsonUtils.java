package io.github.vivek.linkforge.utility;

import tools.jackson.databind.ObjectMapper;

public final class JsonUtils {

    private static ObjectMapper objectMapper;

    private JsonUtils() {
    }

    public static void init(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public static String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize event", e);
        }
    }
}