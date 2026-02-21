package io.github.vivek.linkforge.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redis;

    private static final Duration LOCK_TTL = Duration.ofMinutes(5);

    public boolean acquireLock(String key) {
        var redisKey = buildKey(key);
        return Boolean.TRUE.equals(
                redis.opsForValue().setIfAbsent(redisKey, "LOCKED", LOCK_TTL)
        );
    }

    public void markCompleted(String key) {
        var redisKey = buildKey(key);
        redis.opsForValue().set(redisKey, "COMPLETED", Duration.ofHours(1));
    }

    public void release(String key) {
        redis.delete(buildKey(key));
    }

    private String buildKey(String key) {
        return "idem:" + key;
    }
}