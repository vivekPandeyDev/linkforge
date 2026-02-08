package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.entity.UrlMapping;
import io.github.vivek.linkforge.persistence.UrlMappingPersistence;
import io.github.vivek.linkforge.utility.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

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

    @InjectMocks
    private UrlService urlService;

    @Test
    void shouldGenerateShortenCode() {
        // given
        String longUrl = "https://example.com";
        long snowflakeId = 123456789L;
        String expectedShortCode = Base62.encode(snowflakeId);

        UrlMapping saved = new UrlMapping();
        saved.setId(snowflakeId);
        saved.setShortCode(expectedShortCode);
        saved.setLongUrl(longUrl);
        //  when redis ops for value execute it prevents operation
        doNothing().when(valueOperations).set(anyString(), anyString());  // not mandatory but for learning
        // just returning value operation
        when(redis.opsForValue()).thenReturn(valueOperations);
        when(idGenerator.nextId()).thenReturn(snowflakeId);
        when(persistence.save(expectedShortCode, longUrl)).thenReturn(saved);

        // when
        String result = urlService.generatedShortenCode(longUrl);

        // then
        assertEquals(expectedShortCode, result);

        verify(idGenerator).nextId();
        verify(persistence).save(expectedShortCode, longUrl);
    }

    @Test
    void shouldFailWhenSnowflakeFails() {
        String longUrl = "https://example.com";

        when(idGenerator.nextId())
                .thenThrow(new IllegalStateException("Clock moved backwards"));

        assertThrows(
                IllegalStateException.class,
                () -> urlService.generatedShortenCode(longUrl)
        );

        verifyNoInteractions(persistence);
    }

    @Test
    void shouldFailWhenPersistenceFails() {
        String longUrl = "https://example.com";
        long snowflakeId = 123L;
        String shortCode = Base62.encode(snowflakeId);

        when(idGenerator.nextId()).thenReturn(snowflakeId);
        when(persistence.save(shortCode, longUrl))
                .thenThrow(new RuntimeException("DB unavailable"));

        assertThrows(
                RuntimeException.class,
                () -> urlService.generatedShortenCode(longUrl)
        );

        verify(idGenerator).nextId();
        verify(persistence).save(shortCode, longUrl);
    }


}