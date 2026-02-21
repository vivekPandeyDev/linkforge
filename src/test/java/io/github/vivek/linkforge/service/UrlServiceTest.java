package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.api.advice.LinkNotFoundException;
import io.github.vivek.linkforge.entity.UrlMapping;
import io.github.vivek.linkforge.kafka.RedirectEventProducer;
import io.github.vivek.linkforge.persistence.UrlMappingPersistence;
import io.github.vivek.linkforge.utility.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlMappingPersistence persistence;

    @Mock
    private SnowflakeIdGenerator idGenerator;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedirectEventProducer producer;

    @Mock
    private RBloomFilter<String> bloomFilter;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shouldGenerateShortenCode() {
        // given
        String longUrl = "https://example.com";
        String email = "jo@samaritans.org";
        long snowflakeId = 123456789L;
        String expectedShortCode = Base62.encode(snowflakeId);

        UrlMapping saved = new UrlMapping();
        saved.setId(snowflakeId);
        saved.setShortCode(expectedShortCode);
        saved.setLongUrl(longUrl);
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(idGenerator.nextId()).thenReturn(snowflakeId);
        when(persistence.save(expectedShortCode, longUrl,email)).thenReturn(saved);

        // when
        String result = urlService.generatedShortenCode(longUrl,email);

        // then
        assertEquals(expectedShortCode, result);

        verify(idGenerator).nextId();
        verify(persistence).save(expectedShortCode, longUrl,email);
    }

    @Test
    void shouldFailWhenSnowflakeFails() {
        String longUrl = "https://example.com";
        String email = "jo@samaritans.org";
        when(idGenerator.nextId())
                .thenThrow(new IllegalStateException("Clock moved backwards"));

        assertThrows(
                IllegalStateException.class,
                () -> urlService.generatedShortenCode(longUrl,email)
        );

        verifyNoInteractions(persistence);
    }

    @Test
    void shouldFailWhenPersistenceFails() {
        String longUrl = "https://example.com";
        long snowflakeId = 123L;
        String shortCode = Base62.encode(snowflakeId);
        String email = "jo@samaritans.org";
        when(idGenerator.nextId()).thenReturn(snowflakeId);
        when(persistence.save(shortCode, longUrl,email))
                .thenThrow(new RuntimeException("DB unavailable"));

        assertThrows(
                RuntimeException.class,
                () -> urlService.generatedShortenCode(longUrl,email)
        );

        verify(idGenerator).nextId();
        verify(persistence).save(shortCode, longUrl,email);
    }

    // -------------------------------
    //  Cache Hit 🎯
    // -------------------------------
    @Test
    void shouldReturnCachedValueWhenPresentInRedis() {
        // given
        String code = "abc123";
        String cachedUrl = "https://example.com";

        when(bloomFilter.contains(code)).thenReturn(true);
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("url:" + code)).thenReturn(cachedUrl);

        // when
        String result = urlService.resolvedUrl(code);

        // then
        assertEquals(cachedUrl, result);
        verify(valueOperations).get("url:" + code);
        verifyNoInteractions(persistence);
        verify(valueOperations, never()).set(anyString(), anyString());
    }

    // -------------------------------
    //  Cache MISS 🔍  DB HIT 🎯
    // -------------------------------
    @Test
    void ShouldFetchFromDbAndCacheWhenRedisMiss() {
        // given
        String code = "xyz789";
        String dbUrl = "https://spring.io";
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setId(1L);
        urlMapping.setShortCode(code);
        urlMapping.setLongUrl(dbUrl);
        urlMapping.setCreatedAt(LocalDateTime.now());

        doNothing().when(producer).send(code, dbUrl);

        when(bloomFilter.contains(code)).thenReturn(true);
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("url:" + code)).thenReturn(null);
        when(persistence.findByShortCode(code)).thenReturn(Optional.of(urlMapping));

        // when
        String result = urlService.resolvedUrl(code);

        // then
        assertEquals(dbUrl, result);

        // verify running of get,find,set methods
        verify(valueOperations).get("url:" + code);
        verify(persistence).findByShortCode(code);
        verify(valueOperations).set("url:" + code, dbUrl);
        verify(producer).send(code,dbUrl);
    }

    // -------------------------------
    //  Cache MISS 🔍  DB MISS 🔍
    // -------------------------------
    @Test
    void shouldThrowExceptionWhenUrlNotFound() {
        // given
        String code = "missing";
        when(bloomFilter.contains(code)).thenReturn(true);
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("url:" + code)).thenReturn(null);
        when(persistence.findByShortCode(code)).thenReturn(Optional.empty());

        // when / then
        assertThrows(LinkNotFoundException.class, () -> urlService.resolvedUrl(code));

        verify(valueOperations).get("url:" + code);
        verify(persistence).findByShortCode(code);
        // set operation should not be run
        verify(valueOperations, never()).set(anyString(), anyString());
    }

    @Test
    void shouldRejectImmediatelyWhenBloomSaysNo() {

        String code = "invalid";

        when(bloomFilter.contains(code)).thenReturn(false);

        assertThrows(LinkNotFoundException.class, () -> urlService.resolvedUrl(code));

        verifyNoInteractions(redis);
        verifyNoInteractions(persistence);
    }

}