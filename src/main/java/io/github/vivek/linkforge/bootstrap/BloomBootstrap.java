package io.github.vivek.linkforge.bootstrap;

import io.github.vivek.linkforge.service.BloomRebuildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BloomBootstrap {

    private final RBloomFilter<String> bloomFilter;
    private final BloomRebuildService rebuildService;

    @EventListener(ApplicationReadyEvent.class)
    public void rebuildBloomIfEmpty() {

        long count = bloomFilter.count();

        if (count == 0) {

            log.warn("Bloom filter empty. Rebuilding from DB...");

            rebuildService.rebuildBloom();

            log.info("Bloom rebuild completed.");
        } else {
            log.info("Bloom filter already initialized. Count={}", count);
        }
    }
}