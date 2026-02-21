package io.github.vivek.linkforge.persistence;

import io.github.vivek.linkforge.entity.User;
import io.github.vivek.linkforge.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserPersistence {
    private final UserRepository userRepository;

    public boolean existsByEmail(String email) {
        log.debug("Checking if user with email {}", email);
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        log.info("Saving user {}", user);
        userRepository.save(user);
    }
}
