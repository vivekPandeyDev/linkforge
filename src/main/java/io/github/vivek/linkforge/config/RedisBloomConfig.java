package io.github.vivek.linkforge.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisBloomConfig {

    public static final long EXPECTED_ENTRIES = 10_000_000L;
    public static final double FALSE_POSITIVE_PERCENTAGE = 0.01; // 1% false positive

    @Bean
    public RBloomFilter<String> urlBloomFilter(RedissonClient redissonClient) {

        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("urlShortener:bloom");
        bloomFilter.tryInit(EXPECTED_ENTRIES, FALSE_POSITIVE_PERCENTAGE);
        return bloomFilter;
    }
}