package io.github.vivek.linkforge.api;

import io.github.vivek.linkforge.dto.ShortenRequest;
import io.github.vivek.linkforge.dto.ShortenResponse;
import io.github.vivek.linkforge.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class UrlController {

    private final UrlService service;

    @PostMapping("/shorten")
    public ResponseEntity<@NonNull ShortenResponse> shorten(@Valid @RequestBody ShortenRequest shortenRequest) {
        log.debug("shorten url request: {}", shortenRequest);
        final var shortenResponse = new ShortenResponse(service.generatedShortenCode(shortenRequest.longUrl(), shortenRequest.email()));
        log.debug("shorten url response: {}", shortenResponse);
        return ResponseEntity.ok(shortenResponse);
    }
}
