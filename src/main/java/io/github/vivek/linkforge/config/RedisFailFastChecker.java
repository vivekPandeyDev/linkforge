package io.github.vivek.linkforge.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class RedisFailFastChecker {

    private final StringRedisTemplate redis;

    @PostConstruct
    public void checkRedisConnection() {
        try {
            assert redis.getConnectionFactory() != null;
            redis.getConnectionFactory()
                    .getConnection()
                    .ping();
            log.info("Redis connection verified");
        } catch (Exception ex) {
            log.error("Redis is unavailable. Failing fast.: {}",ex.getMessage(), ex);
            throw new IllegalStateException("Redis is required but not available", ex);
        }
    }
}
