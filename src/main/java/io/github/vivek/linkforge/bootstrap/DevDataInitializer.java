package io.github.vivek.linkforge.bootstrap;


import io.github.vivek.linkforge.entity.Role;
import io.github.vivek.linkforge.entity.User;
import io.github.vivek.linkforge.persistence.UserPersistence;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("dev") // Runs only when dev profile is active
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final UserPersistence persistence;

    @Override
    public void run(String @NonNull ... args) {

        String system = "System";

        createUserIfNotExists(
                "Admin",
                system,
                "admin@example.com",
                Set.of(Role.ADMIN)
        );

        createUserIfNotExists(
                "Manager",
                system,
                "manager@example.com",
                Set.of(Role.MANAGER)
        );

        createUserIfNotExists(
                "User",
                system,
                "user@example.com",
                Set.of(Role.USER)
        );
    }

    private void createUserIfNotExists(String firstName,
                                       String lastName,
                                       String email,
                                       Set<Role> roles) {

        if (persistence.existsByEmail(email)) {
            return;
        }

        var user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("password123"); // default password (should be encoded!)
        user.setRoles(roles);
        user.setEnabled(true);

        persistence.save(user);
    }
}