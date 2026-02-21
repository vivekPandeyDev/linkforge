package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.api.advice.LinkNotFoundException;
import io.github.vivek.linkforge.kafka.RedirectEventProducer;
import io.github.vivek.linkforge.persistence.UrlMappingPersistence;
import io.github.vivek.linkforge.utility.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
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
    private final RedirectEventProducer producer;
    private final RBloomFilter<String> bloomFilter;

    @Transactional
    public String generatedShortenCode(String longUrl,String email) {
        log.debug("Generating snowflake id for url: {}", longUrl);
        final var snowflakeId = idGenerator.nextId();
        final var shortenCode = Base62.encode(snowflakeId);
        log.debug("Generated Snowflake id: {}", snowflakeId);
        log.debug("Generated shorten code: {}", shortenCode);

        var savedUrlMapping = persistence.save(shortenCode, longUrl,email);
        // adding shorten code to bloom filter
        bloomFilter.add(shortenCode);
        // adding shorten code as key
        redis.opsForValue().set("url:" + shortenCode, longUrl);
        return savedUrlMapping.getShortCode();
    }

    public String resolvedUrl(String code) {
        log.debug("code for url shorten: {}", code);
        if (!bloomFilter.contains(code)) {
            throw new LinkNotFoundException(code);
        }
        final var cachedUrl = redis.opsForValue().get("url:" + code);
        if (cachedUrl != null) {
            log.debug("cachedUrl original url for shorten code {} : {}", code, cachedUrl);
            producer.send(code, cachedUrl);
            return cachedUrl;
        }
        final var mapping = persistence.findByShortCode(code).orElseThrow(() -> new LinkNotFoundException(code));
        String longUrl = mapping.getLongUrl();
        log.debug("original url for shorten code {} : {}", code, longUrl);
        redis.opsForValue().set("url:" + code, longUrl);
        producer.send(code, longUrl);
        return longUrl;
    }
}