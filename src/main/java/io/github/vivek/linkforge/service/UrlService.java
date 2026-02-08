package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.persistence.UrlMappingPersistence;
import io.github.vivek.linkforge.utility.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final UrlMappingPersistence persistence;
    private final SnowflakeIdGenerator idGenerator;

    @Transactional
    public String generatedShortenCode(String longUrl) {
        log.info("Generating snowflake id for url: {}", longUrl);
        final var snowflakeId = idGenerator.nextId();
        final var shortenCode = Base62.encode(snowflakeId);
        log.info("Generated Snowflake id: {}", snowflakeId);
        log.info("Generated shorten code: {}", shortenCode);

        var savedUrlMapping = persistence.save(shortenCode, longUrl);

        return savedUrlMapping.getShortCode();
    }

    public String resolvedUrl(String code) {
        log.info("code for url shorten: {}", code);
        return "https://www.google.com";
    }
}