package io.github.vivek.linkforge.repo;

import io.github.vivek.linkforge.entity.Role;
import io.github.vivek.linkforge.entity.UrlMapping;
import io.github.vivek.linkforge.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestConstructor;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // enable constructor dependency injection
@Slf4j
@RequiredArgsConstructor
class UrlRepositoryTest {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;

    private User createUserIfNotExists(Set<Role> roles) {

        if (userRepository.existsByEmail("user@example.com")) {
            return userRepository.findByEmail("user@example.com").orElse(null);
        }

        User user = new User();
        user.setFirstName("User");
        user.setLastName("System");
        user.setEmail("user@example.com");
        user.setPassword("password123"); // default password (should be encoded!)
        user.setRoles(roles);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Test
    void shouldSaveAndFindByShortCode() {
        // given
        User user = createUserIfNotExists(Set.of(Role.USER));
        var mapping = new UrlMapping();
        mapping.setShortCode("abc123");
        mapping.setLongUrl("https://example.com/very/long/path");
        mapping.setUser(user);
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
        User user = createUserIfNotExists(Set.of(Role.USER));
        var first = new UrlMapping();
        first.setShortCode("dup");
        first.setLongUrl("https://example.com/1");
        first.setUser(user);

        var second = new UrlMapping();
        second.setShortCode("dup");
        second.setLongUrl("https://example.com/2");
        second.setUser(user);

        // when
        urlRepository.save(first);

        // then
        assertThrows(
                DataIntegrityViolationException.class,
                () -> urlRepository.saveAndFlush(second)
        );
    }

}