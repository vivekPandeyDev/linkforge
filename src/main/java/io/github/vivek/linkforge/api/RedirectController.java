package io.github.vivek.linkforge.api;

import io.github.vivek.linkforge.service.UrlService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@Validated
@SuppressWarnings("unused")
public class RedirectController {

    private final UrlService service;

    @GetMapping("/{code}")
    public ResponseEntity<@NonNull Void> redirect(@NotBlank(message = "url shorten code should not be empty") @PathVariable String code) {
        log.debug("redirect url code: {}",code);
        final var resolvedUrl = service.resolvedUrl(code);
        return ResponseEntity.status(302)
                .location(URI.create(resolvedUrl))
                .build();
    }
}
