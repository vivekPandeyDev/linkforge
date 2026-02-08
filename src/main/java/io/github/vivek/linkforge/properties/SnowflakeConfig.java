package io.github.vivek.linkforge.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfig {
}
