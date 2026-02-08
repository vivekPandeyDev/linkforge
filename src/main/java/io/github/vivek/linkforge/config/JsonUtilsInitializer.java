package io.github.vivek.linkforge.config;

import io.github.vivek.linkforge.utility.JsonUtils;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class JsonUtilsInitializer {

    public JsonUtilsInitializer(ObjectMapper objectMapper) {
        JsonUtils.init(objectMapper);
    }
}
