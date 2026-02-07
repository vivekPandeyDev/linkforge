package io.github.vivek.linkforge.repo;

import io.github.vivek.linkforge.entity.UrlMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // enable constructor dependency injection
@Slf4j
@RequiredArgsConstructor
class UrlRepositoryTest {

    private final UrlRepository urlRepository;

    @Test
    void shouldSaveAndFindByShortCode() {
        // given
        var mapping = new UrlMapping();
        mapping.setShortCode("abc123");
        mapping.setLongUrl("https://example.com/very/long/path");

        // when
        UrlMapping saved = urlRepository.save(mapping);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        var found = urlRepository.findByShortCode("abc123");

        assertThat(found).isPresent();
        assertThat(found.get().getLongUrl())
                .isEqualTo("https://example.com/very/long/path");
    }

    @Test
    void shouldFailWhenShortCodeIsNotUnique() {
        // given
        var first = new UrlMapping();
        first.setShortCode("dup");
        first.setLongUrl("https://example.com/1");

        var second = new UrlMapping();
        second.setShortCode("dup");
        second.setLongUrl("https://example.com/2");

        // when
        urlRepository.save(first);

        // then
        assertThrows(
                DataIntegrityViolationException.class,
                () -> urlRepository.saveAndFlush(second)
        );
    }

}