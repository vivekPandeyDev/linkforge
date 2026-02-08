package io.github.vivek.linkforge.persistence;

import io.github.vivek.linkforge.entity.UrlMapping;
import io.github.vivek.linkforge.repo.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlMappingPersistence {

    private final UrlRepository repository;


    public UrlMapping save(String shortCode, String longUrl) {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(shortCode);
        mapping.setLongUrl(longUrl);
        UrlMapping saved = repository.save(mapping);

        log.debug("Persisted UrlMapping [id={}, shortCode={}]", saved.getId(), saved.getShortCode());

        return saved;
    }
}
