package io.github.vivek.linkforge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlService {

    public String shorten(String longUrl) {
        return longUrl;
    }

    public String resolve(String code) {
        log.info("code for url shorten: {}", code);
        return "https://www.google.com";
    }
}