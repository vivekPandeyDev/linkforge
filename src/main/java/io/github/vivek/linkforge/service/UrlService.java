package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.repo.UrlRepository;
import io.github.vivek.linkforge.utility.Base62;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository repository;
    private final SnowflakeIdGenerator idGenerator;

    public String generatedShortenCode(String longUrl) {
        log.info("Generating snowflake id for url: {}", longUrl);
        final var snowflakeId = idGenerator.nextId();
        final var shortenCode = Base62.encode(snowflakeId);
        log.info("Generated Snowflake id: {}", snowflakeId);
        log.info("Generated shorten code: {}", shortenCode);
        return shortenCode;
    }

    public String resolvedUrl(String code) {
        log.info("code for url shorten: {}", code);
        return "https://www.google.com";
    }
}