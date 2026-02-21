package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.persistence.UrlMappingPersistence;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BloomRebuildService {

    private final UrlMappingPersistence persistence;
    private final RBloomFilter<String> bloomFilter;

    @Transactional
    public void rebuildBloom() {
        persistence.findAllShortCodes().forEach(bloomFilter::add);
        log.info("Adding rebuild bloom filter: {}", bloomFilter.count());
    }
}