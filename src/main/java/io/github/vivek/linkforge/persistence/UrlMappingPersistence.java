package io.github.vivek.linkforge.persistence;

import io.github.vivek.linkforge.api.advice.UserNotFoundException;
import io.github.vivek.linkforge.entity.UrlMapping;
import io.github.vivek.linkforge.entity.User;
import io.github.vivek.linkforge.repo.UrlRepository;
import io.github.vivek.linkforge.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlMappingPersistence {

    private final UrlRepository repository;
    private final UserRepository userRepository;


    public UrlMapping save(String shortCode, String longUrl, String email) {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode(shortCode);
        mapping.setLongUrl(longUrl);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        mapping.setUser(user);
        var urlMappingSaved = repository.save(mapping);
        log.debug("Persisted UrlMapping [id={}, shortCode={}]", urlMappingSaved.getId(), urlMappingSaved.getShortCode());

        return urlMappingSaved;
    }

    public Optional<UrlMapping> findByShortCode(String shortCode) {
        return repository.findByShortCode(shortCode);
    }

    public Stream<String> findAllShortCodes() {
        return repository.streamAllShortCodes();
    }
}
