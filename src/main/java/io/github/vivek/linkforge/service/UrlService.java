package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.persistence.UrlMappingPersistence;
import io.github.vivek.linkforge.utility.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final UrlMappingPersistence persistence;
    private final SnowflakeIdGenerator idGenerator;
    private final StringRedisTemplate redis;

    @Transactional
    public String generatedShortenCode(String longUrl) {
        log.info("Generating snowflake id for url: {}", longUrl);
        final var snowflakeId = idGenerator.nextId();
        final var shortenCode = Base62.encode(snowflakeId);
        log.info("Generated Snowflake id: {}", snowflakeId);
        log.info("Generated shorten code: {}", shortenCode);

        var savedUrlMapping = persistence.save(shortenCode, longUrl);
        // adding shorten code as key
        redis.opsForValue().set("url:" + shortenCode, longUrl);
        return savedUrlMapping.getShortCode();
    }

    public String resolvedUrl(String code) {
        log.info("code for url shorten: {}", code);
        final var cached = redis.opsForValue().get("url:" + code);
        if (cached != null) {
            log.info("cached original url for shorten code {} : {}", code, cached);
            return cached;
        }
        final var mapping = persistence.findByShortCode(code).orElseThrow();
        String longUrl = mapping.getLongUrl();
        log.info("original url for shorten code {} : {}", code, longUrl);
        redis.opsForValue().set("url:" + code, longUrl);
        return longUrl;
    }
}