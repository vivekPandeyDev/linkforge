package io.github.vivek.linkforge.config;

import io.github.vivek.linkforge.properties.SnowflakeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfig {
}
