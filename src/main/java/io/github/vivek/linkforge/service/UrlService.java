package io.github.vivek.linkforge.service;

import org.springframework.stereotype.Service;

@Service
public class UrlService {
    public String shorten(String longUrl) {
        return longUrl;
    }
}