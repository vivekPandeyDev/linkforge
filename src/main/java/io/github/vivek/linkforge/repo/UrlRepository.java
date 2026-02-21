package io.github.vivek.linkforge.repo;

import io.github.vivek.linkforge.entity.UrlMapping;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;

public interface UrlRepository extends JpaRepository<@NonNull UrlMapping,@NonNull Long> {
    Optional<UrlMapping> findByShortCode(@NonNull String shortCode);
    @Query("select u.shortCode from UrlMapping u")
    Stream<String> streamAllShortCodes();
}